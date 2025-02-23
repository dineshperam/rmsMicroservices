package com.rms.mock;
 
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.rms.model.ContactForm;
import com.rms.model.UserDetails;
import com.rms.repository.ContactFormRepository;
import com.rms.repository.UserDetailsRepository;
import com.rms.service.ContactFormService;
import com.rms.service.EmailService;
import com.rms.exeptions.DuplicateEmailException;
import org.springframework.security.crypto.password.PasswordEncoder;
 
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
 
class ContactFormServiceTest {
 
    @InjectMocks
    private ContactFormService contactFormService;
 
    @Mock
    private ContactFormRepository contactFormRepository;
 
    @Mock
    private UserDetailsRepository userDetailsRepository;
 
    @Mock
    private EmailService emailService;
 
    @Mock
    private PasswordEncoder passwordEncoder;
 
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
 
    // Positive test case for saving a contact form
    @Test
    void testSaveContactForm() {
        ContactForm contactForm = new ContactForm();
        contactForm.setEmail("test@example.com");
        contactForm.setFirstname("John");
        contactForm.setLastname("Doe");
 
        when(contactFormRepository.findByEmail(contactForm.getEmail())).thenReturn(Optional.empty());
        when(userDetailsRepository.findByEmail(contactForm.getEmail())).thenReturn(Optional.empty());
        when(contactFormRepository.save(contactForm)).thenReturn(contactForm);
 
        ContactForm result = contactFormService.saveContactForm(contactForm);
 
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(contactFormRepository, times(1)).save(contactForm);
        verify(emailService, times(1)).sendContactFormConfirmation(contactForm.getEmail(), contactForm.getFirstname());
    }
 
    // Negative test case for duplicate email
    @Test
    void testSaveContactFormDuplicateEmail() {
        ContactForm contactForm = new ContactForm();
        contactForm.setEmail("test@example.com");
 
        when(contactFormRepository.findByEmail(contactForm.getEmail())).thenReturn(Optional.of(new ContactForm()));
        when(userDetailsRepository.findByEmail(contactForm.getEmail())).thenReturn(Optional.empty());
 
        assertThrows(DuplicateEmailException.class, () -> contactFormService.saveContactForm(contactForm));
 
        verify(contactFormRepository, times(0)).save(contactForm);
    }
 
    // Positive test case for accepting a contact request
    @Test
    void testAcceptContactRequest() {
        // Create a mock contact form object
        ContactForm contactForm = new ContactForm();
        contactForm.setEmail("test@example.com");
        contactForm.setFirstname("John");
        contactForm.setLastname("Doe");
        contactForm.setMobileno("1234567890");
        contactForm.setRole("userRole");
 
        // Create a mock UserDetails object
        UserDetails newUser = new UserDetails();
        newUser.setEmail("test@example.com");
        newUser.setFirstName("John");
        newUser.setLastName("Doe");
        newUser.setUsername("doe");
        newUser.setPassword("userRole"); // mock the encoded password
        newUser.setPasswordHash("dummyPassword");
        newUser.setFirstLogin(true);
        newUser.setActive(true);
        newUser.setManagerId(6); // default manager ID
 
        // Mock behavior for the repository and email service
        when(contactFormRepository.findById(1L)).thenReturn(Optional.of(contactForm));
        when(userDetailsRepository.findByEmail(contactForm.getEmail())).thenReturn(Optional.empty());
        when(contactFormRepository.save(contactForm)).thenReturn(contactForm);
        when(userDetailsRepository.save(any(UserDetails.class))).thenReturn(newUser);
 
        // Mock email sending (we assume that it won't actually send an email in the test)
        doNothing().when(emailService).sendWelcomeEmail(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
 
        // Call the method to be tested
        String result = contactFormService.acceptContactRequest(1L, 1);
 
        // Assert the result message
        assertEquals("Contact request accepted, user created, and email sent.", result);
 
        // Verify the interactions with the mock objects
        verify(contactFormRepository, times(1)).save(contactForm);  // Verify save() was called on the contactFormRepository
        verify(userDetailsRepository, times(1)).save(any(UserDetails.class)); // Verify save() was called on the userDetailsRepository
        verify(emailService, times(1)).sendWelcomeEmail(
        	    eq(contactForm.getEmail()), eq(contactForm.getFirstname()), eq("johndoe"),  // Ensure it matches `username`
        	    eq(contactForm.getRole()), eq(contactForm.getEmail()), anyString()); } // Keep `anyString()` for dummy password

 
    // Negative test case for accepting a contact request when the email already exists
    @Test
    void testAcceptContactRequestEmailExists() {
        ContactForm contactForm = new ContactForm();
        contactForm.setEmail("test@example.com");
 
        UserDetails existingUser = new UserDetails();
        existingUser.setEmail("test@example.com");
 
        when(contactFormRepository.findById(1L)).thenReturn(Optional.of(contactForm));
        when(userDetailsRepository.findByEmail(contactForm.getEmail())).thenReturn(Optional.of(existingUser));
 
        String result = contactFormService.acceptContactRequest(1L, 1);
 
        assertEquals("Email already exists. Cannot create a new user.", result);
        verify(contactFormRepository, times(0)).save(contactForm);
        verify(userDetailsRepository, times(0)).save(any(UserDetails.class));
        verify(emailService, times(0)).sendWelcomeEmail(any(), any(), any(), any(), any(), any());
    }
 
    // Positive test case for rejecting a contact request
    @Test
    void testRejectContact() {
        ContactForm contactForm = new ContactForm();
        contactForm.setEmail("test@example.com");
 
        when(contactFormRepository.findById(1L)).thenReturn(Optional.of(contactForm));
        when(contactFormRepository.save(contactForm)).thenReturn(contactForm);
 
        boolean result = contactFormService.rejectContact(1L);
 
        assertTrue(result);
        assertEquals("Rejected", contactForm.getStatus());
        verify(contactFormRepository, times(1)).save(contactForm);
        verify(emailService, times(1)).sendAccountRejectionEmail(contactForm.getEmail(), contactForm.getFirstname(), contactForm.getRole());
    }
 
    // Negative test case for rejecting a contact request when contact form is not found
    @Test
    void testRejectContactNotFound() {
        when(contactFormRepository.findById(1L)).thenReturn(Optional.empty());
 
        boolean result = contactFormService.rejectContact(1L);
 
        assertFalse(result);
        verify(contactFormRepository, times(0)).save(any(ContactForm.class));
        verify(emailService, times(0)).sendAccountRejectionEmail(any(), any(), any());
    }
 
    // Positive test case for showing all contact forms
    @Test
    void testShowContacts() {
        ContactForm contactForm1 = new ContactForm();
        ContactForm contactForm2 = new ContactForm();
        List<ContactForm> mockList = Arrays.asList(contactForm1, contactForm2);
 
        when(contactFormRepository.findAll()).thenReturn(mockList);
 
        List<ContactForm> result = contactFormService.showContacts();
 
        assertEquals(2, result.size());
        verify(contactFormRepository, times(1)).findAll();
    }
    @Test
    void testSaveContactForm_EmailServiceFailure() {
        ContactForm contactForm = new ContactForm();
        contactForm.setEmail("test@example.com");
        contactForm.setFirstname("John");
        contactForm.setLastname("Doe");

        when(contactFormRepository.findByEmail(contactForm.getEmail())).thenReturn(Optional.empty());
        when(userDetailsRepository.findByEmail(contactForm.getEmail())).thenReturn(Optional.empty());
        when(contactFormRepository.save(contactForm)).thenReturn(contactForm);

        // Simulate exception in email service
        doThrow(new RuntimeException("Email service failure"))
                .when(emailService).sendContactFormConfirmation(anyString(), anyString());

        ContactForm result = contactFormService.saveContactForm(contactForm);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(contactFormRepository, times(1)).save(contactForm);

        // Ensure that the exception was caught and logged, but did not break execution
        verify(emailService, times(1)).sendContactFormConfirmation(contactForm.getEmail(), contactForm.getFirstname());
    }
    @Test
    void testAcceptContactRequest_EmailFailure() {
        ContactForm contactForm = new ContactForm();
        contactForm.setEmail("test@example.com");
        contactForm.setFirstname("John");
        contactForm.setLastname("Doe");
        contactForm.setMobileno("1234567890");
        contactForm.setRole("Artist");

        UserDetails newUser = new UserDetails();
        newUser.setEmail("test@example.com");
        newUser.setFirstName("John");
        newUser.setLastName("Doe");
        newUser.setUsername("johndoe");
        newUser.setPassword("encodedPassword");
        newUser.setActive(true);
        newUser.setManagerId(6);

        when(contactFormRepository.findById(1L)).thenReturn(Optional.of(contactForm));
        when(userDetailsRepository.findByEmail(contactForm.getEmail())).thenReturn(Optional.empty());
        when(contactFormRepository.save(contactForm)).thenReturn(contactForm);
        when(userDetailsRepository.save(any(UserDetails.class))).thenReturn(newUser);

        // Simulate email service failure
        doThrow(new RuntimeException("Email sending failed"))
                .when(emailService).sendWelcomeEmail(any(), any(), any(), any(), any(), any());

        String result = contactFormService.acceptContactRequest(1L, 1);

        // Expect that the function completes successfully even if email fails
        assertEquals("Contact request accepted, user created, and email sent.", result);

        verify(contactFormRepository, times(1)).save(contactForm);
        verify(userDetailsRepository, times(1)).save(any(UserDetails.class));

        // Ensure that the exception in email service does not break the process
        verify(emailService, times(1)).sendWelcomeEmail(
        	    eq(contactForm.getEmail()), 
        	    eq(contactForm.getFirstname()), 
        	    eq(contactForm.getFirstname().toLowerCase() + contactForm.getLastname().toLowerCase()), // Correct username format
        	    eq(contactForm.getRole()), 
        	    eq(contactForm.getEmail()), 
        	    anyString() // Dummy password
        	);}


    @Test
    void testAcceptContactRequestNotFound() {
        // Ensure repository returns an empty Optional
        when(contactFormRepository.findById(anyLong())).thenReturn(Optional.empty());

        String result = contactFormService.acceptContactRequest(1L, 1);

        System.out.println("Actual Result: " + result);  // Debugging output

        assertEquals("Contact request not found.", result, "Error: The returned message does not match expected.");
        
        // Ensure no additional interactions happen
        verify(userDetailsRepository, times(0)).save(any(UserDetails.class));
        verify(emailService, times(0)).sendWelcomeEmail(any(), any(), any(), any(), any(), any());
    }


}