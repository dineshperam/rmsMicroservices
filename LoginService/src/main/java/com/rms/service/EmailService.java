package com.rms.service;

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

    public void sendOtpEmail(String to, String otp) {
    	if (Objects.isNull(to) || to.trim().isEmpty()) {
            logger.error("❌ Failed to send email: No recipient found!");
            throw new NotFoundException("Email recipient is missing.");
        }
        logger.info("Sending OTP email to: " + to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp + ". It is valid for 5 minutes.");

        sendEmail(message);
        logger.info("OTP email sent successfully to: " + to);
    }

    public void sendContactFormConfirmation(String to, String firstname) {
        if (Objects.isNull(to) || to.trim().isEmpty()) {
            logger.error("❌ Failed to send email: No recipient found!");
            throw new NotFoundException("Email recipient is missing.");
        }

        if (Objects.isNull(firstname) || firstname.trim().isEmpty()) {
            logger.error("❌ Failed to send email: First name is missing!");
            throw new NotFoundException("First name is missing.");
        }

        logger.info("Sending contact form confirmation email to: " + to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Thank You for Contacting Royal Mint");
        message.setText("Hey " + firstname + ",\n\n"
                + "Thank you for reaching out to Royal Mint. We've received your information and will contact you shortly.\n\n"
                + "Best Regards,\n"
                + "Royal Mint Team");

        sendEmail(message);
        logger.info("Contact form confirmation email sent to: " + to);
    }


    public void sendWelcomeEmail(String to, String firstname, String username, String role, String email, String password) {
        logger.info("Sending welcome email to: " + to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to Royal Mint - Your Account Details");

        String roleText;
        if (role.equalsIgnoreCase("Artist")) {
            roleText = "an Artist";
        } else if (role.equalsIgnoreCase("Manager")) {
            roleText = "a Manager";
        } else if (role.equalsIgnoreCase("Admin")) {
            roleText = "an Admin";
        } else {
            roleText = "a valued member";
        }

        String emailBody = "Greetings " + firstname + ",\n\n"
                + "🎉 Welcome to Royal Mint! We are absolutely thrilled to have you onboard as " + roleText + ". 🎶\n\n"
                + "Your account has been successfully created, and you can now log in using the credentials below:\n\n"
                + "🔹 **Email:** " + email + "\n"
                + "🔹 **Username:** " + username + "\n"
                + "🔹 **Password:** " + password + "\n\n"
                + "**Next Steps:**\n"
                + "✔️ Please log in using email and password at [Royal Mint Portal](http://royalmint.com) and change your password for security.\n"
                + "✔️ After logging in, we encourage you to update your profile details if needed.\n\n"
                + "We look forward to seeing your contributions and making great music together!\n\n"
                + "Best Regards,\n"
                + "🎵 Royal Mint Team 🎵";

        message.setText(emailBody);
        sendEmail(message);
        logger.info("Welcome email sent successfully to: " + to);
    }
    
    public void sendAccountRejectionEmail(String to, String firstname, String role) {
        logger.info("Sending account rejection email to: " + to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Application Status - Account Registration");

        String roleText = role.equalsIgnoreCase("Artist") ? "an Artist" : "a Manager";

        String emailBody = "Hello " + firstname + ",\n\n"
                + "Thank you for your interest in joining Royal Mint as " + roleText + ".\n\n"
                + "Unfortunately, we are unable to register your account at this time. We apologize for any inconvenience this may cause.\n\n"
                + "For further details or any clarifications, please feel free to contact our support team at support@royalmint.com.\n\n"
                + "Best Regards,\n"
                + "🎵 Royal Mint Team 🎵";

        message.setText(emailBody);
        sendEmail(message);
        logger.info("Account rejection email sent successfully to: " + to);
    }


    public void sendPaymentReceivedEmail(String to, String name, double amount) {
        logger.info("Sending payment received email to: " + to + " (Amount: " + amount + ")");
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
    
    public void sendPartnershipRequestEmail(String to, String managerName, String artistName) {
        logger.info("Sending partnership request email to: " + to);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("New Partnership Request - Action Required");

        String emailBody = "Dear " + managerName + ",\n\n"
                + "🎤 You have received a new partnership request from **" + artistName + "**.\n\n"
                + "📌 Please review and respond to the request in your account dashboard.\n"
                + "⏳ If not responded to, the request will automatically expire in **24 hours**.\n\n"
                + "🔗 [Login to Your Account](http://royalmint.com/login)\n\n"
                + "Best Regards,\n"
                + "🎵 Royal Mint Team 🎵";

        message.setText(emailBody);
        sendEmail(message);
        logger.info("Partnership request email sent successfully to: " + to);
    }
    
    public void sendPartnershipResponseEmail(String to, String artistName, String managerName, boolean isAccepted) {
        logger.info("Sending partnership response email to: " + to);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);

        String subject;
        String  emailBody;
        
        if (isAccepted) {
            subject = "Partnership Request Accepted 🎉";
            emailBody = "Hey " + artistName + ",\n\n"
                    + "🎉 Great news! Your partnership request has been **accepted** by **" + managerName + "**.\n\n"
                    + "You are now officially connected. Your manager can now assist you in managing your music career on Royal Mint.\n\n"
                    + "🔗 [Login to Your Account](http://royalmint.com/login) to view the update.\n\n"
                    + "Best Regards,\n"
                    + "🎵 Royal Mint Team 🎵";
        } else {
            subject = "Partnership Request Declined ❌";
            emailBody = "Hi " + artistName + ",\n\n"
                    + "We regret to inform you that your partnership request has been **declined** by **" + managerName + "**.\n\n"
                    + "Feel free to explore other potential managers or contact our support team if you have any concerns.\n\n"
                    + "🔗 [Login to Your Account](http://royalmint.com/login) for more details.\n\n"
                    + "Best Regards,\n"
                    + "🎵 Royal Mint Team 🎵";
        }

        message.setSubject(subject);
        message.setText(emailBody);
        sendEmail(message);
        
        logger.info("Partnership response email sent successfully to: " + to);
    }



    public void sendEmail(SimpleMailMessage message) {
        if (Objects.isNull(message) || 
            Optional.ofNullable(message.getTo()).map(arr -> arr.length == 0).orElse(true)) {
            logger.error("Failed to send email: No recipient found!");
            throw new NotFoundException("Missing email recipient");
        }

        mailSender.send(message);  // This should NOT be called for invalid input
    }
}
