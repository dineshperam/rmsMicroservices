package com.rms;
 
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Date;
import com.rms.model.Partnership;

class PartnershipsTest {
 
    @Test
    void testGettersAndSetters() {
        // Create an instance of Partnership
        Partnership partnership = new Partnership();
        
        // Set values using setters
        partnership.setPartnershipId(1);
        partnership.setArtistId(101);
        partnership.setManagerId(202);
        partnership.setStatus("Active");
        partnership.setPercentage(15.5);
        partnership.setComments("Excellent collaboration");
        partnership.setStartDate(new Date());
        partnership.setEndDate(new Date());
        partnership.setDurationMonths(12);

        // Positive Assertions
        assertEquals(1, partnership.getPartnershipId());
        assertEquals(101, partnership.getArtistId());
        assertEquals(202, partnership.getManagerId());
        assertEquals("Active", partnership.getStatus());
        assertEquals(15.5, partnership.getPercentage());
        assertEquals("Excellent collaboration", partnership.getComments());
        assertNotNull(partnership.getStartDate());
        assertNotNull(partnership.getEndDate());
        assertEquals(12, partnership.getDurationMonths());

        // Negative Assertions
        assertNotEquals(2, partnership.getPartnershipId());
        assertNotEquals(999, partnership.getArtistId());
        assertNotEquals(555, partnership.getManagerId());
        assertNotEquals("Inactive", partnership.getStatus());
        assertNotEquals(20.0, partnership.getPercentage());
        assertNotEquals("Poor collaboration", partnership.getComments());
        assertNotEquals(24, partnership.getDurationMonths());
    }
 
    @Test
    void testConstructor() {
        // Create an instance of Partnership using the constructor
        Date startDate = new Date();
        Date endDate = new Date();
        Partnership partnership = new Partnership(1, 101, 202, "Active", 15.5, "Excellent collaboration", startDate, endDate, 12);

        // Positive Assertions
        assertEquals(1, partnership.getPartnershipId());
        assertEquals(101, partnership.getArtistId());
        assertEquals(202, partnership.getManagerId());
        assertEquals("Active", partnership.getStatus());
        assertEquals(15.5, partnership.getPercentage());
        assertEquals("Excellent collaboration", partnership.getComments());
        assertEquals(startDate, partnership.getStartDate());
        assertEquals(endDate, partnership.getEndDate());
        assertEquals(12, partnership.getDurationMonths());

        // Negative Assertions
        assertNotEquals(999, partnership.getPartnershipId());
        assertNotEquals(555, partnership.getArtistId());
        assertNotEquals(333, partnership.getManagerId());
        assertNotEquals("Pending", partnership.getStatus());
        assertNotEquals(50.0, partnership.getPercentage());
        assertNotEquals("Bad partnership", partnership.getComments());
        
        // Fix: Use a predefined incorrect date for comparison
        Date incorrectDate = new Date(startDate.getTime() - 10000); // 10 seconds earlier
        assertNotEquals(incorrectDate, partnership.getStartDate());
        assertNotEquals(incorrectDate, partnership.getEndDate());

        assertNotEquals(6, partnership.getDurationMonths());
    }

 
    @Test
    void testToString() {
        // Create a sample Partnership object
        Date startDate = new Date();
        Date endDate = new Date();
        Partnership partnership = new Partnership(1, 101, 202, "Active", 15.5, "Excellent collaboration", startDate, endDate, 12);
        
        // Construct the expected toString output
        String expectedToString = "Partnership(partnershipId=1, artistId=101, managerId=202, status=Active, " +
                                  "percentage=15.5, comments=Excellent collaboration, startDate=" + startDate + 
                                  ", endDate=" + endDate + ", durationMonths=12)";
        
        // Positive Assertion
        assertEquals(expectedToString, partnership.toString());

        // Negative Assertion (Different object should not match expected string)
        Partnership differentPartnership = new Partnership(2, 999, 555, "Inactive", 20.0, "Average collaboration", startDate, endDate, 24);
        assertNotEquals(expectedToString, differentPartnership.toString());
    }
}
