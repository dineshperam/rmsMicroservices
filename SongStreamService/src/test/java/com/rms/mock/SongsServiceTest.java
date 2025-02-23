package com.rms.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.rms.CONSTANTS;
import com.rms.exeptions.NotFoundException;
import com.rms.model.Song;
import com.rms.repository.SongRepository;
import com.rms.service.SongService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SongsServiceTest {

    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private SongService songService;

    private Song sampleSong;

    @BeforeEach
    void setUp() {
        sampleSong = new Song();
        sampleSong.setSongId(1);
        sampleSong.setTitle("Test Song");
        sampleSong.setArtistId(100);
    }

    @Test
    void testSongsList_Found() {
        when(songRepository.findAll()).thenReturn(Arrays.asList(sampleSong));
        List<Song> result = songService.songsList();
        assertEquals(1, result.size());
        assertEquals("Test Song", result.get(0).getTitle());
        verify(songRepository, times(1)).findAll();
    }

    @Test
    void testSongsList_Empty() {
        when(songRepository.findAll()).thenReturn(Collections.emptyList());
        List<Song> result = songService.songsList();
        assertTrue(result.isEmpty());
        verify(songRepository, times(1)).findAll();
    }

    @Test
    void testSearchSongById_Found() {
        when(songRepository.findById(1)).thenReturn(Optional.of(sampleSong));
        Song result = songService.searchSongById(1);
        assertEquals("Test Song", result.getTitle());
        verify(songRepository, times(1)).findById(1);
    }

    @Test
    void testSearchSongById_NotFound() {
        when(songRepository.findById(1)).thenReturn(Optional.empty());
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> songService.searchSongById(1));
        assertEquals("Song with ID 1" + CONSTANTS.NOT_FOUND_MSG, thrown.getMessage());
        verify(songRepository, times(1)).findById(1);
    }

    @Test
    void testAddSong_Success() {
        songService.addSong(sampleSong);
        verify(songRepository, times(1)).save(sampleSong);
    }

    @Test
    void testAddSong_EmptyTitle() {
        sampleSong.setTitle("");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> songService.addSong(sampleSong));
        assertEquals("Song title cannot be empty", thrown.getMessage());
    }

    @Test
    void testAddSong_Null() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> songService.addSong(null));
        assertEquals("Song cannot be null", thrown.getMessage());
    }
    


    @Test
    void testUpdateSong_Success() {
        when(songRepository.existsById(1)).thenReturn(true);
        songService.updateSong(sampleSong);
        verify(songRepository, times(1)).save(sampleSong);
    }

    @Test
    void testUpdateSong_NotFound() {
        when(songRepository.existsById(1)).thenReturn(false);
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> songService.updateSong(sampleSong));
        assertEquals("Cannot update, song with ID 1" + CONSTANTS.NOT_FOUND_MSG, thrown.getMessage());
    }

    @Test
    void testUpdateSong_Null() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> songService.updateSong(null));
        assertEquals("Updated song cannot be null", thrown.getMessage());
    }


    @Test
    void testSearchByTitle_Found() {
        when(songRepository.findByTitle("Test Song")).thenReturn(sampleSong);
        Song result = songService.searchByTitle("Test Song");

        assertNotNull(result);
        assertEquals("Test Song", result.getTitle());
        verify(songRepository, times(1)).findByTitle("Test Song");
    }

    @Test
    void testSearchByTitle_NotFound() {
        when(songRepository.findByTitle("Unknown")).thenReturn(null);
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> songService.searchByTitle("Unknown"));

        assertEquals("Song with title 'Unknown' " + CONSTANTS.NOT_FOUND_MSG, thrown.getMessage());
    }

    @Test
    void testSearchByTitle_Null() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> songService.searchByTitle(null));

        assertEquals("Title cannot be null or empty", thrown.getMessage());
    }

    @Test
    void testSearchByTitle_Empty() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> songService.searchByTitle(" "));

        assertEquals("Title cannot be null or empty", thrown.getMessage());
    }


    @Test
    void testSearchByArtistId_Found() {
        when(songRepository.findByArtistId(100)).thenReturn(Arrays.asList(sampleSong));
        List<Song> result = songService.searchByArtistId(100);
        assertEquals(1, result.size());
        verify(songRepository, times(1)).findByArtistId(100);
    }

    @Test
    void testSearchByArtistId_NotFound() {
        when(songRepository.findByArtistId(200)).thenReturn(Collections.emptyList());
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> songService.searchByArtistId(200));
        assertEquals("No songs found for artist with ID 200", thrown.getMessage());
    }
}
