package com.rms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import com.rms.model.Royalty;
import java.util.Date;

class RoyaltyTest {

	@Test
    void testGettersAndSetters() {
        // ✅ Positive Test Case
        Royalty royalty = new Royalty();
        Date date = new Date();
        royalty.setRoyaltyId(1);
        royalty.setSongId(101);
        royalty.setCalculatedDate(date);
        royalty.setTotalStreams(100000L);
        royalty.setRoyaltyAmount(500.75);
        royalty.setArtistId(202);
        royalty.setStatus("Paid");

        assertEquals(1, royalty.getRoyaltyId());
        assertEquals(101, royalty.getSongId());
        assertEquals(date, royalty.getCalculatedDate()); // Match the correct date
        assertEquals(100000L, royalty.getTotalStreams());
        assertEquals(500.75, royalty.getRoyaltyAmount());
        assertEquals(202, royalty.getArtistId());
        assertEquals("Paid", royalty.getStatus());

        // ❌ Negative Test Case (Ensure incorrect values do not match)
        assertNotEquals(2, royalty.getRoyaltyId());
        assertNotEquals(202, royalty.getSongId());

        // Fix for Date comparison
        Date futureDate = new Date(date.getTime() + 1000); // 1 second later to ensure difference
        assertNotEquals(futureDate, royalty.getCalculatedDate()); // Should not match

        assertNotEquals(200000L, royalty.getTotalStreams());
        assertNotEquals(999.99, royalty.getRoyaltyAmount());
        assertNotEquals(303, royalty.getArtistId());
        assertNotEquals("Unpaid", royalty.getStatus());
    }

    @Test
    void testConstructor() {
        // ✅ Positive Test Case
        Date date = new Date();
        Royalty royalty = new Royalty(1, 101, date, 100000L, 500.75, 202, "Paid");

        assertEquals(1, royalty.getRoyaltyId());
        assertEquals(101, royalty.getSongId());
        assertEquals(date, royalty.getCalculatedDate());
        assertEquals(100000L, royalty.getTotalStreams());
        assertEquals(500.75, royalty.getRoyaltyAmount());
        assertEquals(202, royalty.getArtistId());
        assertEquals("Paid", royalty.getStatus());

        // ❌ Negative Test Case
        assertNotEquals(2, royalty.getRoyaltyId());
        assertNotEquals(202, royalty.getSongId());

        // Fix for Date comparison
        Date pastDate = new Date(date.getTime() - 1000); // 1 second earlier to ensure difference
        assertNotEquals(pastDate, royalty.getCalculatedDate()); // Should not match

        assertNotEquals(200000L, royalty.getTotalStreams());
        assertNotEquals(999.99, royalty.getRoyaltyAmount());
        assertNotEquals(303, royalty.getArtistId());
        assertNotEquals("Unpaid", royalty.getStatus());
    }

    @Test
    void testToString() {
        // ✅ Positive Test Case
        Date date = new Date();
        Royalty royalty = new Royalty(1, 101, date, 100000L, 500.75, 202, "Paid");

        String expected = "Royalty(royaltyId=1, songId=101, calculatedDate=" + date + 
                          ", totalStreams=100000, royaltyAmount=500.75, artistId=202, status=Paid)";
        assertEquals(expected, royalty.toString());

        // ❌ Negative Test Case (Ensure incorrect string does not match)
        String incorrectToString = "Royalty(royaltyId=2, songId=202, calculatedDate=" + new Date() + 
                                   ", totalStreams=200000, royaltyAmount=999.99, artistId=303, status=Unpaid)";
        assertNotEquals(incorrectToString, royalty.toString());
    }
}
