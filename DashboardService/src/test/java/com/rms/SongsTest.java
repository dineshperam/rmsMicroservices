
package com.rms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import com.rms.model.Song;

class SongsTest {

    @Test
    void testGettersAndSetters() {
        // ✅ Positive Test Case
        Song song = new Song();
        song.setSongId(1);
        song.setArtistId(1001);
        song.setTitle("Shape of You");
        song.setReleaseDate(LocalDate.of(2017, 3, 3));
        song.setCollaborators("Ed Sheeran");
        song.setGenre("Pop");

        assertEquals(1, song.getSongId());
        assertEquals(1001, song.getArtistId());
        assertEquals("Shape of You", song.getTitle());
        assertEquals(LocalDate.of(2017, 3, 3), song.getReleaseDate());
        assertEquals("Ed Sheeran", song.getCollaborators());
        assertEquals("Pop", song.getGenre());

        // ❌ Negative Test Case (Ensure incorrect values do not match)
        assertNotEquals(2, song.getSongId());
        assertNotEquals(2002, song.getArtistId());
        assertNotEquals("Perfect", song.getTitle());
        assertNotEquals(LocalDate.of(2020, 5, 5), song.getReleaseDate());
        assertNotEquals("Taylor Swift", song.getCollaborators());
        assertNotEquals("Rock", song.getGenre());
    }

    @Test
    void testConstructor() {
        // ✅ Positive Test Case
        Song song = new Song(1, 1001, "Shape of You", LocalDate.of(2017, 3, 3), "Ed Sheeran", "Pop");

        assertEquals(1, song.getSongId());
        assertEquals(1001, song.getArtistId());
        assertEquals("Shape of You", song.getTitle());
        assertEquals(LocalDate.of(2017, 3, 3), song.getReleaseDate());
        assertEquals("Ed Sheeran", song.getCollaborators());
        assertEquals("Pop", song.getGenre());

        // ❌ Negative Test Case (Ensure incorrect values do not match)
        assertNotEquals(2, song.getSongId());
        assertNotEquals(2002, song.getArtistId());
        assertNotEquals("Bad Habits", song.getTitle());
        assertNotEquals(LocalDate.of(2021, 7, 2), song.getReleaseDate());
        assertNotEquals("Dua Lipa", song.getCollaborators());
        assertNotEquals("Jazz", song.getGenre());
    }

    @Test
    void testToString() {
        // ✅ Positive Test Case
        Song song = new Song(1, 1001, "Shape of You", LocalDate.of(2017, 3, 3), "Ed Sheeran", "Pop");

        String expectedToString = "Song(songId=1, artistId=1001, title=Shape of You, releaseDate=2017-03-03, " +
                                  "collaborators=Ed Sheeran, genre=Pop)";
        assertEquals(expectedToString, song.toString());

        // ❌ Negative Test Case (Ensure incorrect string does not match)
        String incorrectToString = "Song(songId=2, artistId=2002, title=Bad Habits, releaseDate=2021-07-02, " +
                                   "collaborators=Dua Lipa, genre=Jazz)";
        assertNotEquals(incorrectToString, song.toString());
    }
}
