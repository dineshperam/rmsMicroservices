package com.rms.mock;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rms.controller.UserDetailsController;
import com.rms.dtos.LoginRequest;
import com.rms.dtos.Response;
import com.rms.exeptions.InvalidCredentialsException;
import com.rms.model.UserDetails;
import com.rms.repository.UserDetailsRepository;
import com.rms.service.EmailService;
import com.rms.service.OtpService;
import com.rms.service.UserDetailsService;

@ExtendWith(MockitoExtension.class)
public class UserDetailsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private OtpService otpService;

    @InjectMocks
    private UserDetailsController userDetailsController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userDetailsController).build();
    }

    @Test
    void testAddUser() throws Exception {
        UserDetails user = new UserDetails();
        doNothing().when(userDetailsService).addUser(any(UserDetails.class));

        mockMvc.perform(post("/user/addUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User added successfully."));
    }

    @Test
    void testSearchUser() throws Exception {
        UserDetails user = new UserDetails();
        when(userDetailsService.searchUser(anyInt())).thenReturn(user);

        mockMvc.perform(get("/user/searchUser/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userDetailsService).deleteUser(anyInt());

        mockMvc.perform(delete("/user/deleteUser/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully."));
    }

    @Test
    void testLoginSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "password");
        Response response = Response.builder()
                .status(200)
                .message("message")
                .build();
        when(userDetailsService.loginUser(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testLoginFailure() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "wrongpassword");
        when(userDetailsService.loginUser(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void testGetManagers() throws Exception {
        List<UserDetails> managers = Arrays.asList(new UserDetails(), new UserDetails());
        when(userDetailsService.findManagers()).thenReturn(managers);

        mockMvc.perform(get("/user/getManagers"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(managers)));
    }
    
    @Test
    void testUpdateUserStatus() throws Exception {
        UserDetails user = new UserDetails();
        user.setUserid(1);
        user.setActive(true);

        when(userDetailsRepository.findById(1)).thenReturn(Optional.of(user));
        when(userDetailsRepository.save(any(UserDetails.class))).thenReturn(user);

        mockMvc.perform(put("/user/updateStatus/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deactivated successfully."));
    }
    
    @Test
    void testUpdatePassword() throws Exception {
        Map<String, String> payload = Map.of("username", "testUser", "newPassword", "newPass", "otp", "123456");
        UserDetails user = new UserDetails();
        when(userDetailsRepository.findByUsername("testUser")).thenReturn(user);
        when(otpService.validateOtp("testUser", "123456")).thenReturn(true);

        mockMvc.perform(put("/user/updatePassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password updated successfully"));
    }
}
