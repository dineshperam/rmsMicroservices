package com.rms.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.rms.dtos.LoginRequest;
import com.rms.dtos.Response;
import com.rms.exeptions.InvalidCredentialsException;
import com.rms.exeptions.NotFoundException;
import com.rms.model.UserDetails;
import com.rms.repository.UserDetailsRepository;
import com.rms.security.JwtUtils;
import com.rms.service.UserDetailsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class UserDetailsServiceTest {

    @InjectMocks
    private UserDetailsService userDetailsService;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /*** ✅ Test addUser() ***/
    @Test
    void testAddUser_Success() {
        UserDetails user = new UserDetails();
        user.setUsername("testUser");

        userDetailsService.addUser(user);

        verify(userDetailsRepository, times(1)).save(user);
    }

    /*** ✅ Test searchUser() ***/
    @Test
    void testSearchUser_Success() {
        UserDetails user = new UserDetails();
        user.setUserid(1);

        when(userDetailsRepository.findById(1)).thenReturn(Optional.of(user));

        UserDetails foundUser = userDetailsService.searchUser(1);

        assertEquals(1, foundUser.getUserid());
    }

    @Test
    void testSearchUser_NotFound() {
        when(userDetailsRepository.findById(1)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> userDetailsService.searchUser(1));

        assertEquals("User is not found with ID: 1", thrown.getMessage()); // Ensure correct message
    }


    /*** ✅ Test updateUser() ***/
    @Test
    void testUpdateUser_Success() {
        UserDetails user = new UserDetails();
        user.setUserid(1);

        when(userDetailsRepository.existsById(1)).thenReturn(true);

        userDetailsService.updateUser(user);

        verify(userDetailsRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUser_NotFound() {
        UserDetails user = new UserDetails();
        user.setUserid(1);

        when(userDetailsRepository.existsById(1)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userDetailsService.updateUser(user));
    }

    /*** ✅ Test deleteUser() ***/
    @Test
    void testDeleteUser_Success() {
        when(userDetailsRepository.existsById(1)).thenReturn(true);

        userDetailsService.deleteUser(1);

        verify(userDetailsRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userDetailsRepository.existsById(1)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userDetailsService.deleteUser(1));
    }

    /*** ✅ Test showUsers() ***/
    @Test
    void testShowUsers_Success() {
        List<UserDetails> users = Arrays.asList(new UserDetails(), new UserDetails());

        when(userDetailsRepository.findAll()).thenReturn(users);

        List<UserDetails> result = userDetailsService.showUsers();

        assertEquals(2, result.size());
    }

    @Test
    void testShowUsers_NoUsersFound() {
        when(userDetailsRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> userDetailsService.showUsers());
    }

    @Test
    void testUpdateUserDetails_Success() {
        UserDetails existingUser = new UserDetails();
        existingUser.setUsername("testUser");
        existingUser.setFirstName("OldFirst");
        existingUser.setLastName("OldLast");
        existingUser.setEmail("old@example.com");

        UserDetails updatedUser = new UserDetails();
        updatedUser.setUsername("testUser");
        updatedUser.setFirstName("NewFirst");
        updatedUser.setLastName("NewLast");
        updatedUser.setEmail("new@example.com");

        when(userDetailsRepository.findByUsername("testUser")).thenReturn(existingUser);

        boolean result = userDetailsService.updateUserDetails(updatedUser);

        assertTrue(result);
        verify(userDetailsRepository).save(existingUser);
        assertEquals("NewFirst", existingUser.getFirstName());
        assertEquals("NewLast", existingUser.getLastName());
        assertEquals("new@example.com", existingUser.getEmail());
    }
    @Test
    void testUpdateUserDetails_UserNotFound() {
        // Mock repository to return null (simulating a user not found)
        when(userDetailsRepository.findByUsername("unknownUser")).thenReturn(null);

        // Create a dummy user object to pass into the method
        UserDetails user = new UserDetails();
        user.setUsername("unknownUser");

        // Expect NotFoundException to be thrown
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> 
            userDetailsService.updateUserDetails(user) // Pass the user object correctly
        );

        // Validate exception message
        assertEquals("User not found with username: unknownUser", thrown.getMessage());
    }

    @Test
    void testSearchByUseName_Success() {
        UserDetails user = new UserDetails();
        user.setUsername("testUser");

        when(userDetailsRepository.findByUsername("testUser")).thenReturn(user);

        UserDetails result = userDetailsService.searchByUseName("testUser");

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void testSearchByUseName_UserNotFound() {
        when(userDetailsRepository.findByUsername("unknownUser")).thenReturn(null);

        UserDetails result = userDetailsService.searchByUseName("unknownUser");

        assertNull(result);
    }

  

    /*** ✅ Test login() ***/
    @Test
    void testLogin_Success() {
        when(userDetailsRepository.countByUsernameAndPassword("user", "password")).thenReturn(1L);

        String result = userDetailsService.login("user", "password");

        assertEquals("1", result);
    }

    @Test
    void testLogin_Failure() {
        when(userDetailsRepository.countByUsernameAndPassword("user", "password")).thenReturn(0L);

        assertThrows(InvalidCredentialsException.class, () -> userDetailsService.login("user", "password"));
    }

    /*** ✅ Test registerUser() ***/
    @Test
    void testRegisterUser_Success() {
        UserDetails user = new UserDetails();
        user.setUsername("testUser");
        user.setPassword("rawPassword");

        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        Response response = userDetailsService.registerUser(user);

        assertEquals(200, response.getStatus());
        verify(userDetailsRepository, times(1)).save(user);
    }

    /*** ✅ Test loginUser() ***/
    @Test
    void testLoginUser_Success() {
        UserDetails user = new UserDetails();
        user.setEmail("test@test.com");
        user.setPassword("hashedPassword");
        user.setRole("Artist");

        when(userDetailsRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correctPassword", "hashedPassword")).thenReturn(true);
        when(jwtUtils.generateToken("test@test.com")).thenReturn("mockToken");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("correctPassword");

        Response response = userDetailsService.loginUser(loginRequest);

        assertEquals(200, response.getStatus());
        assertEquals("mockToken", response.getToken());
    }

    @Test
    void testLoginUser_EmailNotFound() {
        when(userDetailsRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("correctPassword");

        assertThrows(NotFoundException.class, () -> userDetailsService.loginUser(loginRequest));
    }

    @Test
    void testLoginUser_PasswordMismatch() {
        UserDetails user = new UserDetails();
        user.setEmail("test@test.com");
        user.setPassword("hashedPassword");

        when(userDetailsRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("wrongPassword");

        assertThrows(InvalidCredentialsException.class, () -> userDetailsService.loginUser(loginRequest));
    }

    /*** ✅ Test getCurrentLoggedInUser() ***/
    @Test
    void testGetCurrentLoggedInUser_Success() {
        UserDetails user = new UserDetails();
        user.setEmail("test@test.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@test.com");
        when(userDetailsRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        SecurityContextHolder.setContext(securityContext);

        UserDetails result = userDetailsService.getCurrentLoggedInUser();

        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void testGetCurrentLoggedInUser_NotFound() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@test.com");
        when(userDetailsRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        SecurityContextHolder.setContext(securityContext);

        assertThrows(NotFoundException.class, () -> userDetailsService.getCurrentLoggedInUser());
    }
}
