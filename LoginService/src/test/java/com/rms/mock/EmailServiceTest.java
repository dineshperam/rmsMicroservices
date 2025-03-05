package com.rms.mock;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));  //  Prevents actual email sending
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
    void testSendWelcomeEmail_InvalidRole_DefaultsToValuedMember() {
        String to = "test@example.com";
        emailService.sendWelcomeEmail(to, "John", "john_doe", "UnknownRole", "test@example.com", "secure123");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));  //  Ensures email is still sent
    }
    
    @Test
    void testSendWelcomeEmail_FailsWhenRecipientMissing() {
        assertThrows(NotFoundException.class, () -> emailService.sendWelcomeEmail(null, "John", "john_doe", "Artist", "test@example.com", "secure123"));
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
    
    @Test
    void testSendPaymentReceivedEmail() {
        String to = "artist@example.com";
        String name = "John Doe";
        double amount = 500.75;

        emailService.sendPaymentReceivedEmail(to, name, amount);

        verifyEmailSent(to, "Royalty Payment Received", "Greetings, " + name);
    }

    @Test
    void testSendPaymentSentEmail() {
        String to = "manager@example.com";
        String name = "Jane Smith";
        double amount = 300.50;
        String recipientName = "John Doe";

        emailService.sendPaymentSentEmail(to, name, amount, recipientName);

        verifyEmailSent(to, "Royalty Payment Sent", "Dear " + name);
    }

    @Test
    void testSendPartnershipRequestEmail() {
        String to = "manager@example.com";
        String managerName = "Jane Smith";
        String artistName = "John Doe";

        emailService.sendPartnershipRequestEmail(to, managerName, artistName);

        verifyEmailSent(to, "New Partnership Request - Action Required", "Dear " + managerName);
    }

    @Test
    void testSendPartnershipResponseEmail_Accepted() {
        String to = "artist@example.com";
        String artistName = "John Doe";
        String managerName = "Jane Smith";

        emailService.sendPartnershipResponseEmail(to, artistName, managerName, true);

        verifyEmailSent(to, "Partnership Request Accepted 🎉", "Hey " + artistName);
    }

    @Test
    void testSendPartnershipResponseEmail_Declined() {
        String to = "artist@example.com";
        String artistName = "John Doe";
        String managerName = "Jane Smith";

        emailService.sendPartnershipResponseEmail(to, artistName, managerName, false);

        verifyEmailSent(to, "Partnership Request Declined ❌", "Hi " + artistName);
    }

    private void verifyEmailSent(String to, String subject, String expectedBodyStart) {
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertTrue(sentMessage.getText().startsWith(expectedBodyStart), "Email body does not match expected content");
    }
}
