package com.rms.mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import com.rms.security.JwtUtils;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private UserDetails userDetails;

    private String token;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "secret", "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437");
        token = jwtUtils.generateToken("test@example.com");
    }
    
    @Test
    void testSecretNotNull() {
        assertNotNull(ReflectionTestUtils.getField(jwtUtils, "secret"));
    }

    @Test
    void testGenerateToken() {
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {
        String username = jwtUtils.extractUsername(token);
        assertEquals("test@example.com", username);
    }

    @Test
    void testExtractExpiration() {
        Date expiration = jwtUtils.extractExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testValidateToken_ValidToken() {
        when(userDetails.getUsername()).thenReturn("test@example.com");
        assertTrue(jwtUtils.validateToken(token, userDetails));
    }

    @Test
    void testValidateToken_InvalidToken() {
        when(userDetails.getUsername()).thenReturn("wrong@example.com");
        assertFalse(jwtUtils.validateToken(token, userDetails));
    }
}
