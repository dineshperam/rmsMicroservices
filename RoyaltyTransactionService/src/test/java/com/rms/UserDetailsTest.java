package com.rms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import com.rms.model.UserDetails;

class UserDetailsTest {

    @Test
    void testGettersAndSetters() {
        // Create an instance of UserDetails
        UserDetails user = new UserDetails();

        // Set values using setters (Positive Test Cases)
        user.setUserid(1);
        user.setUsername("priya");
        user.setEmail("priya@gmail.com");
        user.setFirstName("Priya");
        user.setLastName("Singh");
        user.setMobileNo("1234567890");
        user.setAddress("123 Street, City");
        user.setRole("Admin");
        user.setPasswordHash("hashed_password");
        user.setPassword("password123");
        user.setManagerId(2);
        user.setActive(true);
        user.setFirstLogin(true);

        // Assert all positive and negative cases in a single block
        assertAll("UserDetails Fields",
            () -> assertEquals(1, user.getUserid()),
            () -> assertEquals("priya", user.getUsername()),
            () -> assertEquals("priya@gmail.com", user.getEmail()),
            () -> assertEquals("Priya", user.getFirstName()),
            () -> assertEquals("Singh", user.getLastName()),
            () -> assertEquals("1234567890", user.getMobileNo()),
            () -> assertEquals("123 Street, City", user.getAddress()),
            () -> assertEquals("Admin", user.getRole()),
            () -> assertEquals("hashed_password", user.getPasswordHash()),
            () -> assertEquals("password123", user.getPassword()),
            () -> assertEquals(2, user.getManagerId()),
            () -> assertTrue(user.isActive()),
            () -> assertTrue(user.isFirstLogin()),

            // Negative Test Cases
            () -> assertNotEquals(2, user.getUserid()),
            () -> assertNotEquals("wrongUser", user.getUsername()),
            () -> assertNotEquals("wrong@gmail.com", user.getEmail()),
            () -> assertNotEquals("John", user.getFirstName()),
            () -> assertNotEquals("Doe", user.getLastName()),
            () -> assertNotEquals("0000000000", user.getMobileNo()),
            () -> assertNotEquals("Unknown Address", user.getAddress()),
            () -> assertNotEquals("User", user.getRole()),
            () -> assertNotEquals("wrong_hash", user.getPasswordHash()),
            () -> assertNotEquals("wrongPass", user.getPassword()),
            () -> assertNotEquals(999, user.getManagerId())
        );
    }

    @Test
    void testConstructor() {
        // Using parameterized constructor
        UserDetails user = new UserDetails(1, "priya", "priya@gmail.com", "Priya", "Singh", 
                                           "1234567890", "123 Street, City", "Admin", 
                                           "hashed_password", "password123", 2, true, true);

        // Assert all positive and negative cases in a single block
        assertAll("UserDetails Constructor",
            () -> assertEquals(1, user.getUserid()),
            () -> assertEquals("priya", user.getUsername()),
            () -> assertEquals("priya@gmail.com", user.getEmail()),
            () -> assertEquals("Priya", user.getFirstName()),
            () -> assertEquals("Singh", user.getLastName()),
            () -> assertEquals("1234567890", user.getMobileNo()),
            () -> assertEquals("123 Street, City", user.getAddress()),
            () -> assertEquals("Admin", user.getRole()),
            () -> assertEquals("hashed_password", user.getPasswordHash()),
            () -> assertEquals("password123", user.getPassword()),
            () -> assertEquals(2, user.getManagerId()),
            () -> assertTrue(user.isActive()),
            () -> assertTrue(user.isFirstLogin()),

            // Negative Test Cases
            () -> assertNotEquals(2, user.getUserid()),
            () -> assertNotEquals("wrongUser", user.getUsername()),
            () -> assertNotEquals("wrong@gmail.com", user.getEmail()),
            () -> assertNotEquals("John", user.getFirstName()),
            () -> assertNotEquals("Doe", user.getLastName()),
            () -> assertNotEquals("0000000000", user.getMobileNo()),
            () -> assertNotEquals("Unknown Address", user.getAddress()),
            () -> assertNotEquals("User", user.getRole()),
            () -> assertNotEquals("wrong_hash", user.getPasswordHash()),
            () -> assertNotEquals("wrongPass", user.getPassword()),
            () -> assertNotEquals(999, user.getManagerId())
        );
    }

    @Test
    void testToString() {
        // Creating an object
        UserDetails user = new UserDetails(1, "priya", "priya@gmail.com", "Priya", "Singh", 
                                           "1234567890", "123 Street, City", "Admin", 
                                           "hashed_password", "password123", 2, true, true);

        // Expected string representation (Ensure it matches actual `toString()` output)
        String expectedToString = "UserDetails(userid=1, username=priya, email=priya@gmail.com, firstName=Priya, " +
                                  "lastName=Singh, mobileNo=1234567890, address=123 Street, City, role=Admin, " +
                                  "passwordHash=hashed_password, password=password123, managerId=2, isActive=true, " +
                                  "firstLogin=true)";

        // Positive Assertion
        assertEquals(expectedToString, user.toString());

        // Negative Test Case: Changing a value should not match the original expected string
        UserDetails differentUser = new UserDetails(2, "john", "john@gmail.com", "John", "Doe",
                                                    "9876543210", "456 Avenue, City", "User",
                                                    "diff_hash", "diffPass", 99, false, false);
        assertNotEquals(expectedToString, differentUser.toString());
    }
}
