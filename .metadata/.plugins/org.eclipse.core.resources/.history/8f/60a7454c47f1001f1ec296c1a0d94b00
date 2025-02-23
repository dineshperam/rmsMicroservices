package com.rms.service;

import com.rms.exeptions.DuplicateEmailException;
import com.rms.model.ContactForm;
import com.rms.model.UserDetails;
import com.rms.repository.ContactFormRepository;
import com.rms.repository.UserDetailsRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ContactFormService {

    private static final Logger logger = Logger.getLogger(ContactFormService.class);
    private ContactFormRepository contactFormRepository;
    private EmailService emailService;
    private UserDetailsRepository userDetailsRepository;
    private PasswordEncoder passwordEncoder;

    private static final Random RANDOM = new Random();

    public ContactFormService(
        ContactFormRepository contactFormRepository,
        EmailService emailService,
        UserDetailsRepository userDetailsRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.contactFormRepository = contactFormRepository;
        this.emailService = emailService;
        this.userDetailsRepository = userDetailsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ContactForm saveContactForm(ContactForm contactForm) {
        logger.info("Saving new contact form for email: " + contactForm.getEmail());

        // Check if email exists in `user_details` or `contactform`
        Optional<UserDetails> existingUser = userDetailsRepository.findByEmail(contactForm.getEmail());
        Optional<ContactForm> existingContact = contactFormRepository.findByEmail(contactForm.getEmail());

        if (existingUser.isPresent() || existingContact.isPresent()) {
            throw new DuplicateEmailException("Email already exists in database.");
        }

        contactForm.setStatus("Pending");
        ContactForm savedForm = contactFormRepository.save(contactForm);
        logger.info("Contact form saved successfully with ID: " + savedForm.getId());

        try {
            emailService.sendContactFormConfirmation(contactForm.getEmail(), contactForm.getFirstname());
            logger.info("Confirmation email sent to: " + contactForm.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send confirmation email to: " + contactForm.getEmail(), e);
        }

        return savedForm;
    }

    public List<ContactForm> showContacts() {
        logger.info("Fetching all contact forms from the database.");
        return contactFormRepository.findAll();
    }

    @Transactional
    public String acceptContactRequest(Long contactId, int adminId) {
        logger.info("Processing acceptance for contact request ID: " + contactId);

        Optional<ContactForm> contactOpt = contactFormRepository.findById(contactId);
        if (contactOpt.isPresent()) {
            ContactForm contact = contactOpt.get();
            logger.info("Found contact request for: " + contact.getEmail());

            // Check if email already exists in UserDetails
            Optional<UserDetails> existingUser = userDetailsRepository.findByEmail(contact.getEmail());
            if (existingUser.isPresent()) {
                logger.warn("Email already exists in the system: " + contact.getEmail());
                return "Email already exists. Cannot create a new user.";
            }

            contact.setStatus("Accepted");
            contactFormRepository.save(contact);
            logger.info("Contact request accepted for: " + contact.getEmail());

            // Create new UserDetails entry
            UserDetails newUser = new UserDetails();
            newUser.setFirstName(contact.getFirstname());
            newUser.setLastName(contact.getLastname());
            newUser.setEmail(contact.getEmail());
            newUser.setMobileNo(contact.getMobileno());
            newUser.setRole(contact.getRole());

            // Generate username and dummy password
            String username = contact.getFirstname().toLowerCase() + contact.getLastname().toLowerCase();
            String email = contact.getEmail();
            String dummyPassword = generateDummyPassword(10);
            newUser.setEmail(email);
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(dummyPassword));
            newUser.setPasswordHash(dummyPassword);
            newUser.setFirstLogin(true);
            newUser.setActive(true);
            newUser.setManagerId(6); // Set default manager ID

            userDetailsRepository.save(newUser);
            logger.info("New user created successfully with username: " + username);

            try {
                emailService.sendWelcomeEmail(contact.getEmail(), contact.getFirstname(), username, contact.getRole(), email, dummyPassword);
                logger.info("Welcome email sent to: " + contact.getEmail());
            } catch (Exception e) {
                logger.error("Failed to send welcome email to: " + contact.getEmail(), e);
            }

            return "Contact request accepted, user created, and email sent.";
        } else {
            logger.warn("Contact request not found for ID: " + contactId);
            return "Contact request not found.";
        } 
    }

    private String generateDummyPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(RANDOM.nextInt(characters.length())));
        }

        logger.info("Generated dummy password of length: " + length);
        return password.toString();
    }

    public boolean rejectContact(Long id) {
        logger.info("Processing rejection for contact request ID: " + id);

        Optional<ContactForm> contactRequest = contactFormRepository.findById(id);
        if (contactRequest.isPresent()) {
            ContactForm contact = contactRequest.get();
            contact.setStatus("Rejected");
            contactFormRepository.save(contact);
            logger.info("Contact request rejected for: " + contact.getEmail());

            // Send rejection email
            emailService.sendAccountRejectionEmail(contact.getEmail(), contact.getFirstname(), contact.getRole());
            logger.info("Rejection email sent to: " + contact.getEmail());

            return true;
        } else {
            logger.warn("Contact request not found for ID: " + id);
            return false;
        }
    }

}
