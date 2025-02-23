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
        if (Objects.isNull(message) || Optional.ofNullable(message.getTo()).map(arr -> arr.length == 0).orElse(true)) {
            logger.error("❌ Failed to send email: No recipient found!");
            throw new NotFoundException("Email recipient is missing.");
        }

        logger.info("📧 Sending email to: " + Arrays.toString(message.getTo()));
        mailSender.send(message);
        logger.info("✅ Email successfully sent to: " + Arrays.toString(message.getTo()));
    }
}
