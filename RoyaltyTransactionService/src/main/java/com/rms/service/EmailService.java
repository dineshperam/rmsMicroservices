package com.rms.service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.rms.exeptions.NotFoundException;

@Service
public class EmailService {

    private static final Logger logger = Logger.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPaymentReceivedEmail(String to, String name, double amount) {
        logger.info("Sending payment received email to: " + to + " (Amount: $" + amount + ")");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Royalty Payment Received");

        String emailBody = "Greetings, " + name + ",\n\n"
                + "🎉 You have received a royalty payment!\n\n"
                + "💰 **Amount Received:** $" + amount + "\n"
                + "📅 **Transaction Date:** " + new java.util.Date() + "\n\n"
                + "Please check your account for further details.\n\n"
                + "Best Regards,\n"
                + "🎵 Royal Mint Team 🎵";

        message.setText(emailBody);
        sendEmail(message);
        logger.info("Payment received email sent successfully to: " + to);
    }

    public void sendPaymentSentEmail(String to, String name, double amount, String recipientName) {
        logger.info("Sending payment sent email to: " + to + " (Recipient: " + recipientName + ", Amount: $" + amount + ")");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Royalty Payment Sent");

        String emailBody = "Dear " + name + ",\n\n"
                + "📢 A royalty payment has been successfully processed.\n\n"
                + "💰 **Amount Sent:** $" + amount + "\n"
                + "🎤 **Recipient:** " + recipientName + "\n"
                + "📅 **Transaction Date:** " + new java.util.Date() + "\n\n"
                + "Best Regards,\n"
                + "🎵 Royal Mint Team 🎵";

        message.setText(emailBody);
        sendEmail(message);
        logger.info("Payment sent email successfully delivered to: " + to);
    }
    


    public void sendEmail(SimpleMailMessage message) {
        if (Objects.isNull(message) || Optional.ofNullable(message.getTo()).map(arr -> arr.length == 0).orElse(true)) {
            logger.error("❌ Failed to send email: No recipient found!");
            throw new NotFoundException("Email recipient is missing.");
        }

        logger.info("📧 Sending email to: " + Arrays.toString(message.getTo()));
        mailSender.send(message);
        logger.info("✅ Email successfully sent to: " + Arrays.toString(message.getTo()));
    }
}
