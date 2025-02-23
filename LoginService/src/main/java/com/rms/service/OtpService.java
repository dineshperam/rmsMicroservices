package com.rms.service;

import org.springframework.stereotype.Service;
import org.apache.log4j.Logger;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {
    private final Map<String, String> otpStorage = new HashMap<>();
    private static final Random random = new SecureRandom();
    
    private static final Logger logger = Logger.getLogger(OtpService.class);

    /**
     * Generates and stores a 6-digit OTP for the given username.
     */
    public synchronized String generateOtp(String username) {
        String otp = String.format("%06d", random.nextInt(999999));
        otpStorage.put(username, otp);
        logger.info("Generated OTP for " + username + ": " + otp);
        return otp;
    }

    /**
     * Validates the OTP for the given username.
     */
    public synchronized boolean validateOtp(String username, String otp) {
        String storedOtp = otpStorage.get(username);

        // Debugging logs
        logger.debug("Validating OTP for " + username);
        logger.debug("Stored OTP: " + storedOtp);
        logger.debug("Provided OTP: " + otp);

        // Ensure storedOtp is not null and matches the provided OTP
        if (storedOtp != null && storedOtp.equals(otp)) {
            logger.info("OTP is valid for " + username);
            return true;
        } else {
            logger.warn("Invalid OTP for " + username);
            return false;
        }
    }

    /**
     * Clears the stored OTP for the given username.
     */
    public synchronized void clearOtp(String username) {
        if (otpStorage.containsKey(username)) {
            logger.info("Clearing OTP for " + username);
            otpStorage.remove(username);
        }
    }
}
