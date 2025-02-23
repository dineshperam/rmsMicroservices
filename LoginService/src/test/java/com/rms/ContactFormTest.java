package com.rms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import com.rms.model.ContactForm;

class ContactFormTest {

    @Test
    void testGettersAndSetters() {
        ContactForm contactForm = new ContactForm();
        contactForm.setId(1L);
        contactForm.setFirstname("John");
        contactForm.setLastname("Doe");
        contactForm.setEmail("john.doe@example.com");
        contactForm.setMobileno("1234567890");
        contactForm.setQuery("Inquiry about product");
        contactForm.setRole("customer");
        contactForm.setStatus("Pending");

        // ✅ Positive Assertions
        assertEquals(1L, contactForm.getId());
        assertEquals("John", contactForm.getFirstname());
        assertEquals("Doe", contactForm.getLastname());
        assertEquals("john.doe@example.com", contactForm.getEmail());
        assertEquals("1234567890", contactForm.getMobileno());
        assertEquals("Inquiry about product", contactForm.getQuery());
        assertEquals("customer", contactForm.getRole());
        assertEquals("Pending", contactForm.getStatus());

        // ❌ Negative Assertions (Ensure incorrect values do not match)
        assertNotEquals(2L, contactForm.getId());
        assertNotEquals("Jane", contactForm.getFirstname());
        assertNotEquals("Smith", contactForm.getLastname());
        assertNotEquals("jane.smith@example.com", contactForm.getEmail());
        assertNotEquals("9876543210", contactForm.getMobileno());
        assertNotEquals("Different query", contactForm.getQuery());
        assertNotEquals("admin", contactForm.getRole());
        assertNotEquals("Resolved", contactForm.getStatus());
    }

    @Test
    void testToString() {
        ContactForm contactForm = new ContactForm(1L, "John", "Doe", "john.doe@example.com", "1234567890", 
                                                   "Inquiry about product", "customer", "Pending");

        // ✅ Positive Assertion
        String expected = "ContactForm(id=1, firstname=John, lastname=Doe, email=john.doe@example.com, " + 
                          "mobileno=1234567890, query=Inquiry about product, role=customer, status=Pending)";
        assertEquals(expected, contactForm.toString());

        // ❌ Negative Assertion (Ensure incorrect string does not match)
        String incorrect = "ContactForm(id=2, firstname=Jane, lastname=Smith, email=jane.smith@example.com, " + 
                           "mobileno=9876543210, query=Wrong query, role=admin, status=Resolved)";
        assertNotEquals(incorrect, contactForm.toString());
    }

    @Test
    void testConstructor() {
        ContactForm contactForm = new ContactForm(1L, "John", "Doe", "john.doe@example.com", "1234567890", 
                                                   "Inquiry about product", "customer", "Pending");

        // ✅ Positive Assertions
        assertEquals(1L, contactForm.getId());
        assertEquals("John", contactForm.getFirstname());
        assertEquals("Doe", contactForm.getLastname());
        assertEquals("john.doe@example.com", contactForm.getEmail());
        assertEquals("1234567890", contactForm.getMobileno());
        assertEquals("Inquiry about product", contactForm.getQuery());
        assertEquals("customer", contactForm.getRole());
        assertEquals("Pending", contactForm.getStatus());

        // ❌ Negative Assertions (Ensure incorrect values do not match)
        assertNotEquals(2L, contactForm.getId());
        assertNotEquals("Alice", contactForm.getFirstname());
        assertNotEquals("Brown", contactForm.getLastname());
        assertNotEquals("alice.brown@example.com", contactForm.getEmail());
        assertNotEquals("1111111111", contactForm.getMobileno());
        assertNotEquals("General feedback", contactForm.getQuery());
        assertNotEquals("manager", contactForm.getRole());
        assertNotEquals("Closed", contactForm.getStatus());
    }
}
