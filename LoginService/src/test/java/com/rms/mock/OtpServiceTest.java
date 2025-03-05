package com.rms.mock;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import com.rms.service.OtpService;

class OtpServiceTest {

    @InjectMocks
    private OtpService otpService;

    @Mock
    private Logger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateOtp() {
        String username = "testUser";
        String otp = otpService.generateOtp(username);
        
        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}"));
    }

    @Test
    void testValidateOtp_Success() {
        String username = "testUser";
        String otp = otpService.generateOtp(username);

        assertTrue(otpService.validateOtp(username, otp));
    }

    @Test
    void testValidateOtp_Failure() {
        String username = "testUser";
        otpService.generateOtp(username);
        
        assertFalse(otpService.validateOtp(username, "123456"));
    }

    @Test
    void testClearOtp() {
        String username = "testUser";
        otpService.generateOtp(username);
        otpService.clearOtp(username);
        
        assertFalse(otpService.validateOtp(username, "123456"));
    }
}
