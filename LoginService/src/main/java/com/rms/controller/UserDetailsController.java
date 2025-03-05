package com.rms.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.rms.dtos.LoginRequest;
import com.rms.dtos.Response;
import com.rms.exeptions.InvalidCredentialsException;
import com.rms.exeptions.NotFoundException;
import com.rms.model.UserDetails;
import com.rms.repository.UserDetailsRepository;
import com.rms.service.EmailService;
import com.rms.service.OtpService;
import com.rms.service.UserDetailsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserDetailsController {

    private final UserDetailsService userDetailsService;
    private final UserDetailsRepository userDetailsRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpService otpService;
    
    private static final String USER_NOT_FOUND = "User Not Found";
    private static final String USERNAME_KEY = "username";

    // Add user
    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@RequestBody UserDetails userDetails) {
        userDetailsService.addUser(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body("User added successfully.");
    }

    // Search user by ID
    @GetMapping("/searchUser/{userid}")
    public ResponseEntity<UserDetails> searchUser(@PathVariable int userid) {
        UserDetails userDetails = userDetailsService.searchUser(userid);
        return ResponseEntity.ok(userDetails);
    }

    // Update user details
    @PutMapping("/update")
    public ResponseEntity<UserDetails> updateUserProfile(@RequestBody UserDetails userDetails) {
        boolean isUpdated = userDetailsService.updateUserDetails(userDetails);
        
        if (isUpdated) {
            UserDetails updatedUser = userDetailsService.searchByUseName(userDetails.getUsername());
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // No content if user not found
        }
    }
    
    // Delete user
    @DeleteMapping("/deleteUser/{userid}")
    public ResponseEntity<String> deleteUser(@PathVariable int userid) {
        userDetailsService.deleteUser(userid);
        return ResponseEntity.ok("User deleted successfully.");
    }


    // Show all users
    @GetMapping("/showUser")
    public ResponseEntity<List<UserDetails>> showUser() {
        List<UserDetails> users = userDetailsService.showUsers();
        return ResponseEntity.ok(users);
    }

    // Login using JWT authentication
    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest) {
        try {
            Response response = userDetailsService.loginUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (InvalidCredentialsException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Response.builder()
                                               .status(401)
                                               .message(e.getMessage())
                                               .build());
        }
    }


    // Get current logged-in user
    @GetMapping("/current")
    public ResponseEntity<UserDetails> getCurrentUser() {
        return ResponseEntity.ok(userDetailsService.getCurrentLoggedInUser());
    }


    // Forgot Password - Send OTP
    @PostMapping("/forgotPassword")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> payload) {
        String username = payload.get(USERNAME_KEY);
        UserDetails user = userDetailsRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.badRequest().body(USER_NOT_FOUND);
        }

        String otp = otpService.generateOtp(username);
        emailService.sendOtpEmail(user.getEmail(), otp);

        return ResponseEntity.ok("OTP sent successfully");
    }

    // Verify OTP
    @PostMapping("/verifyOtp")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get(USERNAME_KEY);
        String otp = (String) payload.get("otp");

        if (otpService.validateOtp(username, otp)) {
            otpService.clearOtp(username);
            return ResponseEntity.ok("OTP verified successfully");
        }
        return ResponseEntity.badRequest().body("Invalid OTP");
    }

    // Update Password
    @PutMapping("/updatePassword")
    public ResponseEntity<String> updatePassword(@RequestBody Map<String, String> payload) {
        String username = payload.get(USERNAME_KEY);
        String newPassword = payload.get("newPassword");
        String otp = payload.get("otp");

        UserDetails user = userDetailsRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body(USER_NOT_FOUND);
        }

        if (!otpService.validateOtp(username, otp)) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFirstLogin(false);
        userDetailsRepository.save(user);
        otpService.clearOtp(username);

        return ResponseEntity.ok("Password updated successfully");
    }

    // Toggle User Status (Activate/Deactivate)
    @PutMapping("/updateStatus/{userId}")
    public ResponseEntity<String> updateUserStatus(@PathVariable int userId) {
        Optional<UserDetails> optionalUser = userDetailsRepository.findById(userId);

        if (optionalUser.isPresent()) {
            UserDetails user = optionalUser.get();
            boolean newStatus = !user.isActive();  
            user.setActive(newStatus);

            userDetailsRepository.save(user);
            return ResponseEntity.ok("User " + (newStatus ? "activated" : "deactivated") + " successfully.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USER_NOT_FOUND);
    }
    
 // Get artists under a specific manager
    @GetMapping("/getArtistUnderManager/{id}")
    public ResponseEntity<List<UserDetails>> getArtistUnderManager(@PathVariable int id) {
        return ResponseEntity.ok(userDetailsService.findArtistsUnderManager(id));
    }
    
    @GetMapping("/getManagers")
    public ResponseEntity<List<UserDetails>> getManagers() {
        return ResponseEntity.ok(userDetailsService.findManagers());
    }
    
}
