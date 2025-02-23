package com.rms.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.rms.CONSTANTS;
import com.rms.exeptions.InvalidStreamException;
import com.rms.exeptions.NotFoundException;
import com.rms.model.Song;
import com.rms.model.Streams;
import com.rms.repository.SongRepository;
import com.rms.repository.StreamsRepository;
import com.rms.service.StreamsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class StreamsServiceTest {

    @Mock
    private StreamsRepository streamRepository;

    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private StreamsService streamsService;

    private Streams sampleStream;
    private Song sampleSong;

    @BeforeEach
    void setUp() {
        sampleStream = new Streams();
        sampleStream.setStreamId(1);
        sampleStream.setSongId(100);
        sampleStream.setStreamCount(200);
        sampleStream.setUserId(500);
        sampleStream.setStreamDate(new Date());

        sampleSong = new Song();
        sampleSong.setSongId(100);
        sampleSong.setArtistId(500);
    }

    // ✅ Fetch all streams - Positive Case
    @Test
    void testShowStream_Found() {
        when(streamRepository.findAll()).thenReturn(Collections.singletonList(sampleStream));
        List<Streams> result = streamsService.showStream();
        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getSongId());
        verify(streamRepository, times(1)).findAll();
    }

    // ✅ Fetch all streams - Negative Case (empty list)
    @Test
    void testShowStream_Empty() {
        when(streamRepository.findAll()).thenReturn(Collections.emptyList());
        List<Streams> result = streamsService.showStream();
        assertTrue(result.isEmpty());
        verify(streamRepository, times(1)).findAll();
    }

    // ✅ Search for a stream by ID - Positive Case
    @Test
    void testSearchStreamById_Found() {
        when(streamRepository.findById(1)).thenReturn(Optional.of(sampleStream));
        Streams result = streamsService.searchStreamById(1);
        assertNotNull(result);
        assertEquals(100, result.getSongId());
        verify(streamRepository, times(1)).findById(1);
    }

    // ✅ Search for a stream by ID - Negative Case (not found)
    @Test
    void testSearchStreamById_NotFound() {
        when(streamRepository.findById(1)).thenReturn(Optional.empty());
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> streamsService.searchStreamById(1));
        assertEquals("Stream with ID 1" + CONSTANTS.NOT_FOUND_MSG, thrown.getMessage());
        verify(streamRepository, times(1)).findById(1);
    }

    // ✅ Add a stream - Positive Case
    @Test
    void testAddStream_Success() {
        streamsService.addStream(sampleStream);
        verify(streamRepository, times(1)).save(sampleStream);
    }

    // ✅ Add a stream - Negative Case (Invalid song ID)
    @Test
    void testAddStream_InvalidSongId() {
        sampleStream.setSongId(0);
        InvalidStreamException thrown = assertThrows(InvalidStreamException.class, () -> streamsService.addStream(sampleStream));
        assertEquals("Stream must be associated with a valid song ID", thrown.getMessage());
    }

    // ✅ Update a stream - Positive Case
    @Test
    void testUpdateStream_Success() {
        when(streamRepository.existsById(1)).thenReturn(true);
        streamsService.updateStream(sampleStream);
        verify(streamRepository, times(1)).save(sampleStream);
    }

    // ✅ Update a stream - Negative Case (not found)
    @Test
    void testUpdateStream_NotFound() {
        when(streamRepository.existsById(1)).thenReturn(false);
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> streamsService.updateStream(sampleStream));
        assertEquals("Cannot update, stream with ID 1" + CONSTANTS.NOT_FOUND_MSG, thrown.getMessage());
    }

    // ✅ Delete a stream - Positive Case
    @Test
    void testDeleteStream_Success() {
        when(streamRepository.existsById(1)).thenReturn(true);
        streamsService.deleteStream(1);
        verify(streamRepository, times(1)).deleteById(1);
    }

    // ✅ Delete a stream - Negative Case (not found)
    @Test
    void testDeleteStream_NotFound() {
        when(streamRepository.existsById(1)).thenReturn(false);
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> streamsService.deleteStream(1));
        assertEquals("Cannot delete, stream with ID 1" + CONSTANTS.NOT_FOUND_MSG, thrown.getMessage());
    }

    // ✅ Search streams by song ID - Positive Case
    @Test
    void testSearchBySongId_Found() {
        when(streamRepository.findBySongId(100)).thenReturn(Collections.singletonList(sampleStream));
        List<Streams> result = streamsService.searchBysongId(100);
        assertEquals(1, result.size());
        verify(streamRepository, times(1)).findBySongId(100);
    }

    // ✅ Search streams by song ID - Negative Case (not found)
    @Test
    void testSearchBySongId_NotFound() {
        when(streamRepository.findBySongId(200)).thenReturn(Collections.emptyList());
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> streamsService.searchBysongId(200));
        assertEquals("No streams found for song with ID 200", thrown.getMessage());
    }

    // ✅ Get in-progress streams - Positive Case
    @Test
    void testGetInProgressStreams_Found() {
        when(streamRepository.findByStatus("IN PROGRESS")).thenReturn(Collections.singletonList(sampleStream));
        List<Streams> result = streamsService.getInProgressStreams();
        assertEquals(1, result.size());
        verify(streamRepository, times(1)).findByStatus("IN PROGRESS");
    }

    // ✅ Get in-progress streams - Negative Case (empty list)
    @Test
    void testGetInProgressStreams_Empty() {
        when(streamRepository.findByStatus("IN PROGRESS")).thenReturn(Collections.emptyList());
        List<Streams> result = streamsService.getInProgressStreams();
        assertTrue(result.isEmpty());
    }

    // ✅ Insert new streams - Verify save calls
    @Test
    void testInsertNewStreams() {
        when(songRepository.findAll()).thenReturn(Collections.singletonList(sampleSong));
        streamsService.insertNewStreams();
        verify(streamRepository, times(1)).save(any(Streams.class));
    }
}
