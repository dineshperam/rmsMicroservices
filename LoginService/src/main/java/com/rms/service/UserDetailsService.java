package com.rms.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rms.CONSTANTS;
import com.rms.dtos.LoginRequest;
import com.rms.dtos.Response;
import com.rms.exeptions.InvalidCredentialsException;
import com.rms.exeptions.NotFoundException;
import com.rms.model.UserDetails;
import com.rms.repository.UserDetailsRepository;
import com.rms.security.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class UserDetailsService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserDetailsService.class);
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
    private final UserDetailsRepository userDetailsRepository;
    private final JwtUtils jwtUtils;

    public UserDetailsService(PasswordEncoder passwordEncoder, 
                       UserDetailsRepository userDetailsRepository, 
                       JwtUtils jwtUtils, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userDetailsRepository = userDetailsRepository;
        this.jwtUtils = jwtUtils;
    }
	
	public void addUser(UserDetails userDetails) {
        logger.info("Adding new user: " + userDetails.getUsername());
		userDetailsRepository.save(userDetails);
        logger.info("User added successfully.");
	}
	
	public UserDetails searchUser(int userid) {
        logger.info("Searching for user with ID: " + userid);
		return userDetailsRepository.findById(userid)
                .orElseThrow(() -> {
                    logger.warn(userid+CONSTANTS.NOT_FOUND_MSG);
                    return new NotFoundException("User is not found with ID: " + userid);
                });
	}
	
	public void updateUser(UserDetails userDetails) {
        logger.info("Updating user with ID: " + userDetails.getUserid());
		if (!userDetailsRepository.existsById(userDetails.getUserid())) {
            logger.warn(userDetails.getUserid()+CONSTANTS.NOT_FOUND_MSG);
            throw new NotFoundException(userDetails.getUserid()+CONSTANTS.NOT_FOUND_MSG);
        }
		userDetailsRepository.save(userDetails);
        logger.info("User updated successfully.");
	}
	
	public void deleteUser(int userid) {
        logger.info("Deleting user with ID: " + userid);
		if (!userDetailsRepository.existsById(userid)) {
            logger.warn("User not found with ID: " + userid);
            throw new NotFoundException("User not found with ID: " + userid);
        }
		userDetailsRepository.deleteById(userid);
        logger.info("User deleted successfully.");
	}
	
	public List<UserDetails> showUsers() {
        logger.info("Fetching all users.");
		List<UserDetails> users = userDetailsRepository.findAll();
        if (users.isEmpty()) {
            logger.warn("No users found.");
            throw new NotFoundException("No users found.");
        }
        return users;
	}
	
	public String login(String username, String password) {
        logger.info("Attempting login for username: " + username);
		long count = userDetailsRepository.countByUsernameAndPassword(username, password);
		if (count == 0) {
            logger.warn("Invalid login attempt for username: " + username);
            throw new InvalidCredentialsException("Invalid username or password.");
        }
        logger.info("Login successful for username: " + username);
		return String.valueOf(count);
	}
	
	public boolean updateUserDetails(UserDetails userDetails) {
        logger.info("Updating user details for username: " + userDetails.getUsername());
        UserDetails user = userDetailsRepository.findByUsername(userDetails.getUsername());

        if (user == null) {
            logger.warn("User not found with username: " + userDetails.getUsername());
            throw new NotFoundException("User not found with username: " + userDetails.getUsername());
        }

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setMobileNo(userDetails.getMobileNo());
        user.setAddress(userDetails.getAddress());

        userDetailsRepository.save(user);
        logger.info("User details updated successfully for username: " + userDetails.getUsername());
        return true;
    }	
	
	public UserDetails searchByUseName(String userName) {
        logger.info("Searching for user by username: " + userName);
		return userDetailsRepository.findByUsername(userName);
	}
	
	public Response registerUser(UserDetails userDetails) {
        logger.info("Registering new user: " + userDetails.getUsername());
        String rawPassword = userDetails.getPassword();
		userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
		userDetails.setActive(true);
		userDetails.setFirstLogin(true);
		userDetailsRepository.save(userDetails);
        logger.info("User registered successfully: " + userDetails.getUsername());
        
        emailService.sendWelcomeEmail(
                userDetails.getEmail(),          // to: recipient's email address
                userDetails.getFirstName(),      // firstname: recipient's first name
                userDetails.getUsername(),       // username: user's username
                userDetails.getRole(),           // role: user role (e.g., Artist or Manager)
                userDetails.getEmail(),          // email: same as the recipient's email address
                rawPassword                      // password: the original password (before encoding)
            );

	    return Response.builder()
	                .status(200)
	                .message("User was successfully registered")
	                .build();
	}

    public Response loginUser(LoginRequest loginRequest) {
        logger.info("Logging in user with email: " + loginRequest.getEmail());
    	UserDetails user = userDetailsRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Email not found: " + loginRequest.getEmail());
                    return new NotFoundException("Email Not Found");
                });

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.warn("Invalid password attempt for email: " + loginRequest.getEmail());
            throw new InvalidCredentialsException("Password Does Not Match");
        }
        String token = jwtUtils.generateToken(user.getEmail());

        logger.info("User logged in successfully with email: " + loginRequest.getEmail());

        return Response.builder()
                .status(200)
                .message("User Logged in Successfully")
                .role(user.getRole())
                .token(token)
                .expirationTime("30 minutes")
                .isActive(user.isActive())
                .isFirstLogin(user.isFirstLogin())
                .userId(user.getUserid())
                .managerId(user.getManagerId())
                .firstName(user.getFirstName())
                .build();
    }

    public UserDetails getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        logger.info("Fetching currently logged-in user with email: " + email);

        return userDetailsRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: " + email);
                    return new NotFoundException("User Not Found");
                });
    }
    
    public List<UserDetails> findArtistsUnderManager(int managerId) {
        logger.info("Fetching artists under manager ID: " + managerId);
		List<UserDetails> artists = userDetailsRepository.findByManagerId(managerId);
        if (artists.isEmpty()) {
            logger.warn("No artists found under manager with ID: " + managerId);
            throw new NotFoundException("No artists found under manager with ID: " + managerId);
        }
        return artists;
	}
    
    public List<UserDetails> findManagers() {
        logger.info("Fetching all managers.");
		return userDetailsRepository.findByRole("Manager");
	}
}