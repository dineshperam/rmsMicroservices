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
    void testSendPartnershipRequestEmail_Success() {
        String to = "test@example.com";
        emailService.sendPartnershipRequestEmail(to, "ManagerName", "ArtistName");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendPartnershipResponseEmail_Accepted() {
        String to = "test@example.com";
        emailService.sendPartnershipResponseEmail(to, "ArtistName", "ManagerName", true);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendPartnershipResponseEmail_Rejected() {
        String to = "test@example.com";
        emailService.sendPartnershipResponseEmail(to, "ArtistName", "ManagerName", false);
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
