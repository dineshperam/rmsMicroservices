package com.rms.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rms.dtos.ManagerRoyaltyDTO;
import com.rms.dtos.ManagerRoyaltyDetailsDTO;
import com.rms.dtos.SongDTO;
import com.rms.dtos.UserDTO;
import com.rms.model.Royalty;
import com.rms.model.Song;
import com.rms.model.Streams;
import com.rms.model.Transactions;
import com.rms.model.UserDetails;
import com.rms.repository.RoyaltyRepository;
import com.rms.repository.SongRepository;
import com.rms.repository.StreamsRepository;
import com.rms.repository.TransactionRepository;
import com.rms.repository.UserDetailsRepository;
import com.rms.service.InsightsService;

@ExtendWith(MockitoExtension.class)
class InsightsServiceTest {

    @Mock
    private StreamsRepository streamsRepository;
    
    @Mock
    private SongRepository songRepository;
    
    @Mock
    private RoyaltyRepository royaltyRepository;
    
    @Mock
    private TransactionRepository transactionsRepository;

    @InjectMocks
    private InsightsService insightsService;
    
    @Mock
    private UserDetailsRepository userRepository;

    @BeforeEach
    void setUp() {
        // Setup runs before each test case
    }

    @Test
    void testGetTopStreamedSongs() {
        // Mocking Streams data (most streamed songs)
        List<Streams> mockStreams = List.of(
            new Streams(1, 101, new Date(), 500L, 1, "Active"),
            new Streams(2, 102, new Date(), 400L, 2, "Active"),
            new Streams(3, 103, new Date(), 300L, 3, "Active")
        );

        // Mocking corresponding Song data
        List<Song> mockSongs = List.of(
            new Song(101, 1, "Song A", LocalDate.of(2023, 5, 1), "Artist X", "Pop"),
            new Song(102, 2, "Song B", LocalDate.of(2022, 8, 10), "Artist Y", "Rock"),
            new Song(103, 3, "Song C", LocalDate.of(2021, 1, 15), "Artist Z", "Jazz")
        );

        // Mock repository responses
        when(streamsRepository.findTop3ByOrderByStreamCountDesc()).thenReturn(mockStreams);
        when(songRepository.findAllById(List.of(101, 102, 103))).thenReturn(mockSongs);

        // Call the service method
        List<Song> result = insightsService.getTopStreamedSongs();

        // Assertions
        assertEquals(3, result.size());
        assertEquals("Song A", result.get(0).getTitle());
        assertEquals("Song B", result.get(1).getTitle());
        assertEquals("Song C", result.get(2).getTitle());
    }

    @Test
    void testGetTotalStreamsPerArtist() {
        // Mock Song Data
        List<Song> songs = List.of(
            new Song(101, 1, "Song A", LocalDate.of(2023, 5, 1), "Artist X", "Pop"),
            new Song(102, 1, "Song B", LocalDate.of(2022, 8, 10), "Artist X", "Rock"),
            new Song(103, 2, "Song C", LocalDate.of(2021, 1, 15), "Artist Y", "Jazz")
        );

        // Mock Streams Data
        List<Streams> streams = List.of(
            new Streams(1, 101, new Date(), 500L, 1, "Active"),
            new Streams(2, 102, new Date(), 300L, 2, "Active"),
            new Streams(3, 103, new Date(), 200L, 3, "Active")
        );

        when(songRepository.findAll()).thenReturn(songs);
        when(streamsRepository.findBySongIdIn(List.of(101, 102))).thenReturn(streams.subList(0, 2));
        when(streamsRepository.findBySongIdIn(List.of(103))).thenReturn(streams.subList(2, 3));

        // Call the service method
        Map<Integer, Long> result = insightsService.getTotalStreamsPerArtist();

        // Assertions
        assertEquals(800L, result.get(1)); // Artist X (500 + 300)
        assertEquals(200L, result.get(2)); // Artist Y (200)
    }

    @Test
    void testGetTotalStreams() {
        when(streamsRepository.getTotalStreams()).thenReturn(1000);

        Integer result = insightsService.getTotalStreams();

        assertEquals(1000, result);
    }
    @Test
    void testGetTotalTransactionsPerManager() {
        // Mock transactions
        List<Transactions> transactions = List.of(
            new Transactions(1, 2, 101, new Date(), 500.0, 10, "Credit"),
            new Transactions(3, 4, 102, new Date(), 700.0, 10, "Credit"),
            new Transactions(5, 6, 103, new Date(), 400.0, 11, "Debit")
        );

        when(transactionsRepository.findAll()).thenReturn(transactions);

        Map<Integer, Double> result = insightsService.getTotalTransactionsPerManager();

        assertEquals(2, result.size());
        assertEquals(1200.0, result.get(10)); // Manager 10 = 500 + 700
        assertEquals(400.0, result.get(11)); // Manager 11 = 400
    }

    /** Test - Get Top 3 Earning Artists **/
    @Test
    void testGetTopEarningArtists() {
        List<Royalty> royalties = List.of(
            new Royalty(1, 101, new Date(), 500, 1000.0, 10, "Paid"),
            new Royalty(2, 102, new Date(), 300, 800.0, 11, "Paid"),
            new Royalty(3, 103, new Date(), 700, 1500.0, 10, "Paid"),
            new Royalty(4, 104, new Date(), 400, 900.0, 12, "Paid")
        );

        when(royaltyRepository.findAll()).thenReturn(royalties);

        Map<Integer, Double> result = insightsService.getTopEarningArtists();

        assertEquals(3, result.size());
        assertEquals(2500.0, result.get(10)); // Artist 10 = 1000 + 1500
        assertEquals(900.0, result.get(12));  // Artist 12 = 900
        assertEquals(800.0, result.get(11));  // Artist 11 = 800
    }

    /** Test - Get Total Royalties Paid **/
    @Test
    void testGetTotalRoyaltiesPaid() {
        List<Royalty> royalties = List.of(
            new Royalty(1, 101, new Date(), 500, 1000.0, 10, "Paid"),
            new Royalty(2, 102, new Date(), 300, 800.0, 11, "Paid"),
            new Royalty(3, 103, new Date(), 700, 1500.0, 10, "Paid")
        );

        when(royaltyRepository.findAll()).thenReturn(royalties);

        double result = insightsService.getTotalRoyaltiesPaid();

        assertEquals(3300.0, result);
    }

    /** Test - Get Total Songs per Artist **/
    @Test
    void testGetTotalSongsPerArtist() {
        List<Song> songs = List.of(
            new Song(101, 10, "Song A", LocalDate.of(2023, 5, 1), "Artist X", "Pop"),
            new Song(102, 10, "Song B", LocalDate.of(2022, 8, 10), "Artist X", "Rock"),
            new Song(103, 11, "Song C", LocalDate.of(2021, 1, 15), "Artist Y", "Jazz"),
            new Song(104, 12, "Song D", LocalDate.of(2020, 12, 5), "Artist Z", "Hip-Hop")
        );

        when(songRepository.findAll()).thenReturn(songs);

        Map<Integer, Long> result = insightsService.getTotalSongsPerArtist();

        assertEquals(2, result.get(10)); // Artist 10 = 2 songs
        assertEquals(1, result.get(11)); // Artist 11 = 1 song
        assertEquals(1, result.get(12)); // Artist 12 = 1 song
    }

    /** Test - Get Total Streams per Song **/
    @Test
    void testGetTotalStreamsPerSong() {
        List<Song> songs = List.of(
            new Song(101, 10, "Song A", LocalDate.of(2023, 5, 1), "Artist X", "Pop"),
            new Song(102, 11, "Song B", LocalDate.of(2022, 8, 10), "Artist Y", "Rock")
        );

        when(songRepository.findAll()).thenReturn(songs);
        when(streamsRepository.countBySongId(101)).thenReturn(500L);
        when(streamsRepository.countBySongId(102)).thenReturn(300L);

        Map<Integer, Long> result = insightsService.getTotalStreamsPerSong();

        assertEquals(500, result.get(101)); // Song 101 = 500 streams
        assertEquals(300, result.get(102)); // Song 102 = 300 streams
    }
    @Test
    void testGetTopArtistsByRevenueUnderManager() {
        int managerId = 10;
        List<Object[]> mockResults = List.of(
            new Object[]{1, "Artist A", 5000.0},
            new Object[]{2, "Artist B", 3000.0}
        );

        when(userRepository.findTopArtistsByRevenueUnderManager(managerId)).thenReturn(mockResults);

        List<Object[]> result = insightsService.getTopArtistsByRevenueUnderManager(managerId);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0)[0]);
        assertEquals("Artist A", result.get(0)[1]);
        assertEquals(5000.0, result.get(0)[2]);
    }

    /** Test - Get Monthly Stream Count **/
    @Test
    void testGetMonthlyStreamCount() {
        int month = 2, year = 2025;
        
        // Create streams with correct parameters
        Streams stream1 = new Streams(1, 101, new Date(), 1, 500); // 500 streams for song 101
        Streams stream2 = new Streams(2, 102, new Date(), 1, 300); 

        // Mock the repository response
        when(streamsRepository.findByStreamDateBetween(any(Date.class), any(Date.class)))
            .thenReturn(List.of(stream1, stream2));

        // Call the method
        Map<Integer, Long> result = insightsService.getMonthlyStreamCount(month, year);

        assertEquals(2, result.size());
        assertEquals(500L, result.get(101)); // Now expecting 500 instead of 1
        assertEquals(300L, result.get(102));// Expecting count of 1 for songId 102
    }

    /** Test - Get Total Revenue per User **/
    @Test
    void testGetTotalRevenuePerUser() {
        List<Transactions> transactions = List.of(
            new Transactions(1, 2, 101, new Date(), 1000.0, 10, "Credit"),
            new Transactions(1, 3, 102, new Date(), 2000.0, 10, "Credit"),
            new Transactions(2, 4, 103, new Date(), 1500.0, 11, "Debit")
        );

        when(transactionsRepository.findAll()).thenReturn(transactions);

        Map<Integer, Double> result = insightsService.getTotalRevenuePerUser();

        assertEquals(2, result.size());
        assertEquals(3000.0, result.get(1)); // User 1 = 1000 + 2000
        assertEquals(1500.0, result.get(2)); // User 2 = 1500
    }

    /** Test - Get Artist with Most Collaborations **/
    @Test
    void testGetTopArtistsByCollaborations() {
        List<Song> songs = List.of(
            new Song(101, 10, "Song A", LocalDate.of(2023, 5, 1), "Artist X", "Pop"),
            new Song(102, 10, "Song B", LocalDate.of(2022, 8, 10), "Artist X", "Rock"),
            new Song(103, 11, "Song C", LocalDate.of(2021, 1, 15), "Artist Y", "Jazz"),
            new Song(104, 10, "Song D", LocalDate.of(2020, 12, 5), "Artist X", "Hip-Hop")
        );

        when(songRepository.findByCollaboratorsIsNotNull()).thenReturn(songs);

        List<Integer> result = insightsService.getTopArtistsByCollaborations();

        assertEquals(2, result.size());
        assertEquals(10, result.get(0)); // Artist 10 = 3 collaborations
        assertEquals(11, result.get(1)); // Artist 11 = 1 collaboration
    }

    /** Test - Get Active Artists **/
    @Test
    void testGetActiveArtists() {
        List<UserDetails> activeArtists = List.of(
            new UserDetails(1, "artist1", "a1@example.com", "Artist", "One", "1234567890", "Address", "Artist", "hash", "pass", 0, true, false),
            new UserDetails(2, "artist2", "a2@example.com", "Artist", "Two", "1234567890", "Address", "Artist", "hash", "pass", 0, true, false)
        );

        when(userRepository.findActiveArtists()).thenReturn(activeArtists);

        List<UserDetails> result = insightsService.getActiveArtists();

        assertEquals(2, result.size());
        assertEquals("artist1", result.get(0).getUsername());
        assertEquals("artist2", result.get(1).getUsername());
    }

    /** Test - Get Active Managers **/
    @Test
    void testGetActiveManagers() {
        List<UserDetails> activeManagers = List.of(
            new UserDetails(10, "manager1", "m1@example.com", "Manager", "One", "1234567890", "Address", "Manager", "hash", "pass", 0, true, false),
            new UserDetails(11, "manager2", "m2@example.com", "Manager", "Two", "1234567890", "Address", "Manager", "hash", "pass", 0, true, false)
        );

        when(userRepository.findActiveManagers()).thenReturn(activeManagers);

        List<UserDetails> result = insightsService.getActiveManagers();

        assertEquals(2, result.size());
        assertEquals("manager1", result.get(0).getUsername());
        assertEquals("manager2", result.get(1).getUsername());
    }

    /** Test - Get Top 5 Managers by Total Royalty **/
    @Test
    void testGetTop5ManagersByTotalRoyalty() {
        List<Object[]> mockResults = List.of(
            new Object[]{10, "Manager", "One", "manager1", 5000.0},
            new Object[]{11, "Manager", "Two", "manager2", 4500.0},
            new Object[]{12, "Manager", "Three", "manager3", 4000.0},
            new Object[]{13, "Manager", "Four", "manager4", 3500.0},
            new Object[]{14, "Manager", "Five", "manager5", 3000.0}
        );

        when(userRepository.findTop5ManagersByTotalRoyalty()).thenReturn(mockResults);

        List<ManagerRoyaltyDTO> result = insightsService.getTop5ManagersByTotalRoyalty();

        assertEquals(5, result.size());
        assertEquals(10, result.get(0).getManagerId());
        assertEquals("manager1", result.get(0).getUsername());
        assertEquals(5000.0, result.get(0).getTotalRoyalty());
    }
    
    @Test
    void testGetTop5ArtistsByTotalStreams() {
        List<Object[]> mockData = List.of(
            new Object[]{"John", "Doe", 101, 5000L},
            new Object[]{"Jane", "Smith", 102, 4500L},
            new Object[]{"Alice", "Johnson", 103, 4000L},
            new Object[]{"Bob", "Brown", 104, 3500L},
            new Object[]{"Charlie", "Davis", 105, 3000L}
        );

        when(streamsRepository.findTop5ArtistsByTotalStreams()).thenReturn(mockData);

        List<Map<String, Object>> result = insightsService.getTop5ArtistsByTotalStreams();

        assertEquals(5, result.size());
        assertEquals("John", result.get(0).get("firstName"));
        assertEquals(101, result.get(0).get("artistId"));
        assertEquals(5000L, result.get(0).get("totalStreams"));
    }
    @Test
    void testGetActiveArtistsCount() {
        when(userRepository.countActiveArtists()).thenReturn(10L);

        Long activeArtists = insightsService.getActiveArtistsCount();

        assertEquals(10L, activeArtists);
    }

    @Test
    void testGetActiveManagersCount() {
        when(userRepository.countActiveManagers()).thenReturn(5L);

        Long activeManagers = insightsService.getActiveManagersCount();

        assertEquals(5L, activeManagers);
    }
    @Test
    void testGetFormattedTop5ArtistsByMonth() {
        int year = 2025;
        List<Object[]> mockData = List.of(
            new Object[]{1, "John Doe", 1, BigDecimal.valueOf(2000)},
            new Object[]{2, "Jane Smith", 2, BigDecimal.valueOf(1500)},
            new Object[]{3, "Alice Johnson", 3, BigDecimal.valueOf(1800)}
        );

        when(streamsRepository.findTop5ArtistsByStreams(year)).thenReturn(mockData);

        List<Map<String, Object>> result = insightsService.getFormattedTop5ArtistsByMonth(year);

        assertEquals(12, result.size());
        assertEquals("Jan", result.get(0).get("month"));
        assertEquals(2000L, result.get(0).get("John Doe"));
    }
    @Test
    void testGetAllArtists() {
        List<Object[]> mockData = List.of(
            new Object[]{101, "John Doe", "john@example.com", "Artist", "Active"},
            new Object[]{102, "Jane Smith", "jane@example.com", "Artist", "Inactive"}
        );

        when(streamsRepository.findTop5ArtistsByStreamsDetails()).thenReturn(mockData);

        List<Map<String, Object>> result = insightsService.getAllArtists();

        assertEquals(2, result.size());
        assertEquals(101, result.get(0).get("id"));
        assertEquals("John Doe", result.get(0).get("name"));
    }

    @Test
    void testGetTop5ManagersByRoyaltyForYear() {
        int year = 2025;
        List<Object[]> mockData = List.of(
            new Object[]{"Manager A", 5000.0},
            new Object[]{"Manager B", 4500.0}
        );

        when(royaltyRepository.findTop5ManagersByRoyaltyForMonth(eq(year), anyInt())).thenReturn(mockData);

        List<Map<String, Object>> result = insightsService.getTop5ManagersByRoyaltyForYear(year);

        assertEquals(12, result.size());  // 12 months
        assertEquals("January", result.get(0).get("month"));
        assertEquals("Manager A", result.get(0).get("manager1_name"));
        assertEquals(5000.0, result.get(0).get("manager1"));
    }
    @Test
    void testGetTop5ManagersByRoyalty() {
        int year = 2025;
        List<Object[]> mockData = List.of(
            new Object[]{"John Doe", "john@example.com", "Manager", true, 10000.0},
            new Object[]{"Jane Smith", "jane@example.com", "Manager", false, 9000.0}
        );

        when(royaltyRepository.findTop5ManagersByRoyaltyForYear(year)).thenReturn(mockData);

        List<ManagerRoyaltyDetailsDTO> result = insightsService.getTop5ManagersByRoyalty(year);

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
        assertEquals(10000.0, result.get(0).getTotalRoyalty());
    }
    @Test
    void testGetAllUsers() {
        List<UserDTO> mockUsers = List.of(
            new UserDTO(101, "John Doe", "john@example.com", "Artist", true),
            new UserDTO(102, "Jane Smith", "jane@example.com", "Manager", true)
        );

        when(userRepository.findAllUsers()).thenReturn(mockUsers);

        List<UserDTO> result = insightsService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
    }
    @Test
    void testGetTotalUserCount() {
        when(userRepository.getTotalUserCount()).thenReturn(50L);

        long count = insightsService.getTotalUserCount();

        assertEquals(50L, count);
    }

    @Test
    void testGetTotalActiveUserCount() {
        when(userRepository.getTotalActiveUserCount()).thenReturn(30L);

        long count = insightsService.getTotalActiveUserCount();

        assertEquals(30L, count);
    }
    @Test
    void testGetTotalSongsByArtist() {
        Long artistId = 101L;
        when(songRepository.countByArtistId(artistId)).thenReturn(10L);

        long result = insightsService.getTotalSongsByArtist(artistId);

        assertEquals(10L, result);
    }
    @Test
    void testGetTotalRoyaltyByArtistId() {
        Long artistId = 101L;
        when(royaltyRepository.getTotalRoyaltyByArtistId(artistId)).thenReturn(5000.0);

        double result = insightsService.getTotalRoyaltyByArtistId(artistId);

        assertEquals(5000.0, result);
    }
    @Test
    void testGetTotalStreamsByUserId() {
        int userId = 201;
        when(streamsRepository.getTotalStreamsByUserId(userId)).thenReturn(1500L);

        long result = insightsService.getTotalStreamsByUserId(userId);

        assertEquals(1500L, result);
    }
    @Test
    void testGetTotalSongsByManager() {
        int managerId = 301;
        when(userRepository.countTotalSongsByManager(managerId)).thenReturn(20L);

        long result = insightsService.getTotalSongsByManager(managerId);

        assertEquals(20L, result);
    }
    @Test
    void testGetTotalStreamsByManager() {
        int managerId = 301;
        when(streamsRepository.countTotalStreamsByManager(managerId)).thenReturn(8000L);

        long result = insightsService.getTotalStreamsByManager(managerId);

        assertEquals(8000L, result);
    }
    @Test
    void testGetManagerTotalRevenue() {
        int managerId = 401;
        when(transactionsRepository.getManagerTotalRevenue(managerId)).thenReturn(20000.0);

        double result = insightsService.getManagerTotalRevenue(managerId);

        assertEquals(20000.0, result);
    }
    @Test
    void testGetGenreSongCountByArtist() {
        Long artistId = 101L;
        List<Object[]> mockData = List.of(
            new Object[]{"Pop", 5L},
            new Object[]{"Rock", 3L}
        );

        when(songRepository.getGenreSongCountByArtist(artistId)).thenReturn(mockData);

        Map<String, Long> result = insightsService.getGenreSongCountByArtist(artistId);

        assertEquals(2, result.size());
        assertEquals(5L, result.get("Pop"));
    }

    @Test
    void testGetTopSongsByStreams() {
        int artistId = 101;

        // Convert Date to LocalDate
        LocalDate releaseDate1 = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate releaseDate2 = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Creating Song objects
        Song song1 = new Song();
        song1.setSongId(1);
        song1.setTitle("Song A");
        song1.setGenre("Pop");
        song1.setReleaseDate(releaseDate1); // Corrected type
        song1.setCollaborators("Artist X");

        Song song2 = new Song();
        song2.setSongId(2);
        song2.setTitle("Song B");
        song2.setGenre("Rock");
        song2.setReleaseDate(releaseDate2); // Corrected type
        song2.setCollaborators("Artist Y");

        List<Song> mockSongs = List.of(song1, song2);
        List<Object[]> mockTopSongs = List.of(new Object[]{1}, new Object[]{2});

        when(songRepository.findByArtistId(artistId)).thenReturn(mockSongs);
        when(streamsRepository.findTopSongsByStreams()).thenReturn(mockTopSongs);
        when(songRepository.findById(1)).thenReturn(Optional.of(song1));
        when(songRepository.findById(2)).thenReturn(Optional.of(song2));

        List<SongDTO> result = insightsService.getTopSongsByStreams(artistId);

        // Assertions
        assertEquals(2, result.size());
        assertEquals("Song A", result.get(0).getTitle());
        assertEquals("Pop", result.get(0).getGenre());
        assertEquals("Artist X", result.get(0).getCollaborators());
        assertEquals("Song B", result.get(1).getTitle());
        assertEquals("Rock", result.get(1).getGenre());
        assertEquals("Artist Y", result.get(1).getCollaborators());
    }
    
    @Test
    void testGetTotalRevenueOfArtistsByManager() {
        int managerId = 101;
        double expectedRevenue = 5000.75;

        // Mocking the repository method
        when(royaltyRepository.getTotalRevenueOfArtistsByManager(managerId)).thenReturn(expectedRevenue);

        // Calling the actual method
        Double result = insightsService.getTotalRevenueOfArtistsByManager(managerId);

        // Assertions
        assertEquals(expectedRevenue, result, 0.01); // Allowing minor floating-point differences
    }



}
