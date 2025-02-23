package com.rms.mock;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.rms.exeptions.NotFoundException;
import com.rms.service.EmailService;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendOtpEmail_Success() {
        String to = "test@example.com";
        String otp = "123456";
        emailService.sendOtpEmail(to, otp);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendOtpEmail_FailsWhenRecipientMissing() {
        assertThrows(NotFoundException.class, () -> emailService.sendOtpEmail("", "123456"));
        assertThrows(NotFoundException.class, () -> emailService.sendOtpEmail(null, "123456"));
    }
    @Test
    void testSendContactFormConfirmation_Success() {
        String to = "test@example.com";
        String firstname = "John";
        emailService.sendContactFormConfirmation(to, firstname);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendContactFormConfirmation_FailsWhenRecipientMissing() {
        assertThrows(NotFoundException.class, () -> emailService.sendContactFormConfirmation("", "John"));
        assertThrows(NotFoundException.class, () -> emailService.sendContactFormConfirmation(null, "John"));
    }

    @Test
    void testSendContactFormConfirmation_FailsWhenFirstnameMissing() {
        String to = "test@example.com";
        assertThrows(NotFoundException.class, () -> emailService.sendContactFormConfirmation(to, ""));
        assertThrows(NotFoundException.class, () -> emailService.sendContactFormConfirmation(to, null));
    }

    @Test
    void testSendWelcomeEmail_AsArtist() {
        String to = "test@example.com";
        String firstname = "John";
        String username = "john_doe";
        String role = "Artist";
        String email = "test@example.com";
        String password = "secure123";

        emailService.sendWelcomeEmail(to, firstname, username, role, email, password);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendWelcomeEmail_AsManager() {
        String to = "test@example.com";
        String firstname = "John";
        String username = "john_doe";
        String role = "Manager";
        String email = "test@example.com";
        String password = "secure123";

        emailService.sendWelcomeEmail(to, firstname, username, role, email, password);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendWelcomeEmail_InvalidRole_DefaultsToManager() {
        String to = "test@example.com";
        emailService.sendWelcomeEmail(to, "John", "john_doe", "UnknownRole", "test@example.com", "secure123");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendAccountRejectionEmail_Success() {
        String to = "test@example.com";
        emailService.sendAccountRejectionEmail(to, "John", "Artist");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }


    @Test
    void testSendEmail_FailsWhenNoRecipient() {
        SimpleMailMessage message = new SimpleMailMessage();
        assertThrows(NotFoundException.class, () -> emailService.sendEmail(message));

        message.setTo(new String[]{});
        assertThrows(NotFoundException.class, () -> emailService.sendEmail(message));
    }

    @Test
    void testSendEmail_FailsWhenMessageIsNull() {
        assertThrows(NotFoundException.class, () -> emailService.sendEmail(null));
    }

    @Test
    void testSendEmail_Success() {
        String to = "test@example.com";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Test Email");
        message.setText("This is a test email.");

        emailService.sendEmail(message);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
