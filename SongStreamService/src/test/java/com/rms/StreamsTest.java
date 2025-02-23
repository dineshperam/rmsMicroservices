package com.rms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.Date;
import com.rms.model.Streams;

class StreamsTest {

    @Test
    void testGettersAndSetters() {
        // ✅ Positive Test Case
        Streams stream = new Streams();
        stream.setStreamId(1);
        stream.setSongId(1001);
        stream.setStreamDate(new Date(1672444800000L)); // Fixed Date: 2023-01-01
        stream.setUserId(5001);
        stream.setStreamCount(1000);
        stream.setStatus("Active");

        assertEquals(1, stream.getStreamId());
        assertEquals(1001, stream.getSongId());
        assertEquals(new Date(1672444800000L), stream.getStreamDate());
        assertEquals(5001, stream.getUserId());
        assertEquals(1000, stream.getStreamCount());
        assertEquals("Active", stream.getStatus());

        // ❌ Negative Test Case (Ensure incorrect values do not match)
        assertNotEquals(2, stream.getStreamId());
        assertNotEquals(2002, stream.getSongId());
        assertNotEquals(new Date(1672531200000L), stream.getStreamDate()); // Different Date
        assertNotEquals(6001, stream.getUserId());
        assertNotEquals(5000, stream.getStreamCount());
        assertNotEquals("Inactive", stream.getStatus());
    }

    @Test
    void testConstructor() {
        // ✅ Positive Test Case
        Streams stream = new Streams(1, 1001, new Date(1672444800000L), 5001, 1000);

        assertEquals(1, stream.getStreamId());
        assertEquals(1001, stream.getSongId());
        assertEquals(new Date(1672444800000L), stream.getStreamDate());
        assertEquals(5001, stream.getUserId());
        assertEquals(1000, stream.getStreamCount());

        // ❌ Negative Test Case (Ensure incorrect values do not match)
        assertNotEquals(2, stream.getStreamId());
        assertNotEquals(2002, stream.getSongId());
        assertNotEquals(new Date(1672531200000L), stream.getStreamDate()); // Different Date
        assertNotEquals(6001, stream.getUserId());
        assertNotEquals(5000, stream.getStreamCount());
    }

    @Test
    void testToString() {
        // ✅ Create a fixed Date
        Date fixedDate = new Date(1672444800000L);

        // ✅ Create Streams Object
        Streams stream = new Streams(1, 1001, fixedDate, 5001, 1000);

        // ✅ Ensure that the generated toString contains key values
        String streamToString = stream.toString();

        assertTrue(streamToString.contains("streamId=1"));
        assertTrue(streamToString.contains("songId=1001"));
        assertTrue(streamToString.contains("userId=5001"));
        assertTrue(streamToString.contains("streamCount=1000"));
        assertTrue(streamToString.contains("streamDate=")); // Ensure date exists

        // ❌ Ensure incorrect values are not present
        assertFalse(streamToString.contains("streamId=2"));
        assertFalse(streamToString.contains("songId=2002"));
        assertFalse(streamToString.contains("userId=6001"));
        assertFalse(streamToString.contains("streamCount=5000"));
    }

}
