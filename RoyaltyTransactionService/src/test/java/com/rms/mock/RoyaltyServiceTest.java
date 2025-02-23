package com.rms.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.rms.exeptions.NotFoundException;
import com.rms.model.Partnership;
import com.rms.model.Royalty;
import com.rms.model.Streams;
import com.rms.model.Transactions;
import com.rms.model.UserDetails;
import com.rms.repository.PartnershipRepository;
import com.rms.repository.RoyaltyRepository;
import com.rms.repository.StreamsRepository;
import com.rms.repository.UserDetailsRepository;
import com.rms.service.EmailService;
import com.rms.service.RoyaltyService;
import com.rms.service.TransactionService;

class RoyaltyServiceTest {

    @InjectMocks
    private RoyaltyService royaltyService;

    @Mock
    private RoyaltyRepository royaltyRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private StreamsRepository streamsRepository;

    @Mock
    private PartnershipRepository partnershipRepository;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowRoyalty_Success() {
        List<Royalty> expectedRoyalties = Arrays.asList(new Royalty(), new Royalty());
        when(royaltyRepository.findAll()).thenReturn(expectedRoyalties);

        List<Royalty> result = royaltyService.showRoyalty();

        assertEquals(2, result.size());
        verify(royaltyRepository).findAll();
    }

    @Test
    void testSearchRoyaltyById_Success() {
        Royalty expectedRoyalty = new Royalty();
        expectedRoyalty.setRoyaltyId(1);
        when(royaltyRepository.findById(1)).thenReturn(Optional.of(expectedRoyalty));

        Royalty result = royaltyService.searchRoyaltyById(1);

        assertEquals(1, result.getRoyaltyId());
        verify(royaltyRepository).findById(1);
    }

    @Test
    void testSearchRoyaltyById_NotFound() {
        when(royaltyRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> royaltyService.searchRoyaltyById(1));
        verify(royaltyRepository).findById(1);
    }

    @Test
    void testAddRoyalty_Success() {
        Royalty royalty = new Royalty();
        when(royaltyRepository.save(any(Royalty.class))).thenReturn(royalty);

        royaltyService.addRoyalty(royalty);

        verify(royaltyRepository).save(royalty);
    }

    @Test
    void testUpdateRoyalty_Success() {
        Royalty royalty = new Royalty();
        royalty.setRoyaltyId(1);
        when(royaltyRepository.save(any(Royalty.class))).thenReturn(royalty);

        royaltyService.updateRoyalty(royalty);

        verify(royaltyRepository).save(royalty);
    }

    @Test
    void testSearchByArtistId_Success() {
        List<Royalty> expectedRoyalties = Arrays.asList(new Royalty(), new Royalty());
        when(royaltyRepository.findByArtistId(1)).thenReturn(expectedRoyalties);

        List<Royalty> result = royaltyService.searchByartistId(1);

        assertEquals(2, result.size());
        verify(royaltyRepository).findByArtistId(1);
    }

    @Test
    void testSearchByArtistId_NotFound() {
        when(royaltyRepository.findByArtistId(1)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> royaltyService.searchByartistId(1));
        verify(royaltyRepository).findByArtistId(1);
    }

    @Test
    void testSearchBySongId_Success() {
        List<Royalty> expectedRoyalties = Arrays.asList(new Royalty(), new Royalty());
        when(royaltyRepository.findBySongId(1)).thenReturn(expectedRoyalties);

        List<Royalty> result = royaltyService.searchBysongId(1);

        assertEquals(2, result.size());
        verify(royaltyRepository).findBySongId(1);
    }

    @Test
    void testSearchBySongId_NotFound() {
        when(royaltyRepository.findBySongId(1)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> royaltyService.searchBysongId(1));
        verify(royaltyRepository).findBySongId(1);
    }

    @Test
    void testCalculateRoyaltyAmount_LessThan10000() {
        double result = royaltyService.calculateRoyaltyAmount(5000);
        assertEquals(10.0, result);
    }

    @Test
    void testCalculateRoyaltyAmount_Between10000And50000() {
        double result = royaltyService.calculateRoyaltyAmount(20000);
        assertEquals(70.0, result);
    }

    @Test
    void testCalculateRoyaltyAmount_MoreThan50000() {
        double result = royaltyService.calculateRoyaltyAmount(60000);
        System.out.println("Calculated Royalty Amount: " + result);
        assertEquals(520.0, result,0.01);
    }
    @Test
    void testCalculateAndStoreRoyalty_Success() {
        Streams stream = new Streams();
        stream.setSongId(1);
        stream.setUserId(1);
        stream.setStreamCount(5000);

        List<Streams> streamsList = Collections.singletonList(stream);
        when(streamsRepository.findByStatus("IN PROGRESS")).thenReturn(streamsList);
        when(royaltyRepository.save(any(Royalty.class))).thenReturn(new Royalty());

        royaltyService.calculateAndStoreRoyalty();

        verify(streamsRepository).findByStatus("IN PROGRESS");
        verify(royaltyRepository).save(any(Royalty.class));
    }

    @Test
    void testProcessRoyaltyPayment_Success() {
        // Setup test data
        Royalty royalty = new Royalty();
        royalty.setRoyaltyId(1);
        royalty.setArtistId(2);
        royalty.setRoyaltyAmount(1000.0);

        UserDetails artist = new UserDetails();
        artist.setUserid(2);
        artist.setManagerId(3);
        artist.setEmail("artist@test.com");
        artist.setFirstName("Artist");

        UserDetails manager = new UserDetails();
        manager.setUserid(3);
        manager.setEmail("manager@test.com");
        manager.setFirstName("Manager");

        UserDetails admin = new UserDetails();
        admin.setUserid(1);
        admin.setEmail("admin@test.com");
        admin.setFirstName("Admin");

        Partnership partnership = new Partnership();
        partnership.setPercentage(20.0);

        // Setup mocks
        when(royaltyRepository.findById(1)).thenReturn(Optional.of(royalty));
        when(userDetailsRepository.findById(2)).thenReturn(Optional.of(artist));
        when(userDetailsRepository.findById(3)).thenReturn(Optional.of(manager));
        when(userDetailsRepository.findById(1)).thenReturn(Optional.of(admin));
        when(partnershipRepository.findByArtistIdAndManagerIdAndStatus(2, 3, "Accepted"))
                .thenReturn(Optional.of(partnership));
        when(streamsRepository.findSongIdsByRoyaltyId(1)).thenReturn(Arrays.asList(1, 2));

        // Execute
        royaltyService.processRoyaltyPayment(1, 1);

        // Verify
        verify(transactionService, times(2)).addTransaction(any(Transactions.class));
        verify(emailService, times(2)).sendPaymentReceivedEmail(anyString(), anyString(), anyDouble());
        verify(emailService, times(2)).sendPaymentSentEmail(anyString(), anyString(), anyDouble(), anyString());
        verify(royaltyRepository).save(any(Royalty.class));
        verify(streamsRepository).updateStatusBySongIds(anyList(), eq("PROCESSED"));

    }

    @Test
    void testProcessRoyaltyPayment_RoyaltyNotFound() {
        when(royaltyRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> royaltyService.processRoyaltyPayment(1, 1));
        
        verify(transactionService, never()).addTransaction(any(Transactions.class));
        verify(emailService, never()).sendPaymentReceivedEmail(anyString(), anyString(), anyDouble());
    }

    @Test
    void testProcessRoyaltyPayment_ArtistNotFound() {
        Royalty royalty = new Royalty();
        royalty.setRoyaltyId(1);
        royalty.setArtistId(2);

        when(royaltyRepository.findById(1)).thenReturn(Optional.of(royalty));
        when(userDetailsRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> royaltyService.processRoyaltyPayment(1, 1));
        
        verify(transactionService, never()).addTransaction(any(Transactions.class));
        verify(emailService, never()).sendPaymentReceivedEmail(anyString(), anyString(), anyDouble());
    }

    @Test
    void testProcessRoyaltyPayment_AdminNotFound() {
        Royalty royalty = new Royalty();
        royalty.setRoyaltyId(1);
        royalty.setArtistId(2);

        UserDetails artist = new UserDetails();
        artist.setUserid(2);

        when(royaltyRepository.findById(1)).thenReturn(Optional.of(royalty));
        when(userDetailsRepository.findById(2)).thenReturn(Optional.of(artist));
        when(userDetailsRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> royaltyService.processRoyaltyPayment(1, 1));
        
        verify(transactionService, never()).addTransaction(any(Transactions.class));
        verify(emailService, never()).sendPaymentReceivedEmail(anyString(), anyString(), anyDouble());
    }

    @Test
    void testProcessRoyaltyPayment_ManagerNotFound() {
        // Arrange
        int royaltyId = 1;
        int adminId = 1;
        int artistId = 2;
        int managerId = 3;
        double royaltyAmount = 1000.0;
        double managerSharePercentage = 20.0;

        Royalty royalty = new Royalty();
        royalty.setRoyaltyId(royaltyId);
        royalty.setArtistId(artistId);
        royalty.setRoyaltyAmount(royaltyAmount);

        UserDetails artist = new UserDetails();
        artist.setUserid(artistId);
        artist.setManagerId(managerId);
        artist.setEmail("artist@test.com");
        artist.setFirstName("Artist");

        UserDetails admin = new UserDetails();
        admin.setUserid(adminId);
        admin.setEmail("admin@test.com");
        admin.setFirstName("Admin");

        Partnership partnership = new Partnership();
        partnership.setPercentage(managerSharePercentage);

        // Mocking Repository Calls
        when(royaltyRepository.findById(royaltyId)).thenReturn(Optional.of(royalty));
        when(userDetailsRepository.findById(artistId)).thenReturn(Optional.of(artist)); // Artist exists
        when(userDetailsRepository.findById(adminId)).thenReturn(Optional.of(admin)); // Admin exists
        when(userDetailsRepository.findById(managerId)).thenReturn(Optional.empty()); // Manager does NOT exist
        when(partnershipRepository.findByArtistIdAndManagerIdAndStatus(artistId, managerId, "Accepted"))
                .thenReturn(Optional.of(partnership));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> royaltyService.processRoyaltyPayment(royaltyId, adminId));

        
        verify(transactionService, never()).addTransaction(any(Transactions.class));
        verify(emailService, never()).sendPaymentReceivedEmail(anyString(), anyString(), anyDouble());
    }



    @Test
    void testProcessRoyaltyPayment_WithoutManagerShare() {
        // Setup test data
        Royalty royalty = new Royalty();
        royalty.setRoyaltyId(1);
        royalty.setArtistId(2);
        royalty.setRoyaltyAmount(1000.0);

        UserDetails artist = new UserDetails();
        artist.setUserid(2);
        artist.setManagerId(3);
        artist.setEmail("artist@test.com");
        artist.setFirstName("Artist");

        UserDetails admin = new UserDetails();
        admin.setUserid(1);
        admin.setEmail("admin@test.com");
        admin.setFirstName("Admin");

        // Setup mocks - Return empty for partnership to simulate no manager share
        when(royaltyRepository.findById(1)).thenReturn(Optional.of(royalty));
        when(userDetailsRepository.findById(2)).thenReturn(Optional.of(artist));
        when(userDetailsRepository.findById(1)).thenReturn(Optional.of(admin));
        when(partnershipRepository.findByArtistIdAndManagerIdAndStatus(2, 3, "Accepted"))
                .thenReturn(Optional.empty()); // No partnership exists
        when(streamsRepository.findSongIdsByRoyaltyId(1)).thenReturn(Arrays.asList(1, 2));

        // Execute
        royaltyService.processRoyaltyPayment(1, 1);

        // Verify only artist transaction was created
        verify(transactionService, times(1)).addTransaction(argThat(transaction -> 
            transaction.getManagerId()== 2 && transaction.getTransactionAmount() == 1000.0
        ));
        
        // Verify only artist emails were sent
        verify(emailService, times(1)).sendPaymentReceivedEmail(anyString(), anyString(), anyDouble());
        verify(emailService, times(1)).sendPaymentSentEmail(anyString(), anyString(), anyDouble(), anyString());
        
        // Verify royalty status update
        verify(royaltyRepository).save(argThat(r -> "PAID".equals(r.getStatus())));
        verify(streamsRepository).updateStatusBySongIds(anyList(), eq("PROCESSED"));
    }

    @Test
    void testProcessRoyaltyPayment_WithZeroManagerShare() {
        // Setup test data
        Royalty royalty = new Royalty();
        royalty.setRoyaltyId(1);
        royalty.setArtistId(2);
        royalty.setRoyaltyAmount(1000.0);

        UserDetails artist = new UserDetails();
        artist.setUserid(2);
        artist.setManagerId(3);
        artist.setEmail("artist@test.com");
        artist.setFirstName("Artist");

        UserDetails admin = new UserDetails();
        admin.setUserid(1);
        admin.setEmail("admin@test.com");
        admin.setFirstName("Admin");

        Partnership partnership = new Partnership();
        partnership.setPercentage(0.0); // Zero percentage partnership

        // Setup mocks
        when(royaltyRepository.findById(1)).thenReturn(Optional.of(royalty));
        when(userDetailsRepository.findById(2)).thenReturn(Optional.of(artist));
        when(userDetailsRepository.findById(1)).thenReturn(Optional.of(admin));
        when(partnershipRepository.findByArtistIdAndManagerIdAndStatus(2, 3, "Accepted"))
                .thenReturn(Optional.of(partnership));
        when(streamsRepository.findSongIdsByRoyaltyId(1)).thenReturn(Arrays.asList(1, 2));

        // Execute
        royaltyService.processRoyaltyPayment(1, 1);

     // Verify only artist transaction was created
        verify(transactionService, times(1)).addTransaction(argThat(transaction -> 
            transaction.getReceiver() == artist.getUserid() && transaction.getTransactionAmount() == 1000.0
        ));

        // Verify only artist emails were sent
        verify(emailService, times(1)).sendPaymentReceivedEmail(anyString(), anyString(), anyDouble());
        verify(emailService, times(1)).sendPaymentSentEmail(anyString(), anyString(), anyDouble(), anyString());

        // Verify royalty status update
        verify(royaltyRepository).save(argThat(r -> r != null && "PAID".equals(r.getStatus())));
        verify(streamsRepository).updateStatusBySongIds(anyList(), eq("PROCESSED"));

    }

    @Test
    void testProcessRoyaltyPayment_WithManagerShare() {
        // Setup test data
        Royalty royalty = new Royalty();
        royalty.setRoyaltyId(1);
        royalty.setArtistId(2);
        royalty.setRoyaltyAmount(1000.0);

        UserDetails artist = new UserDetails();
        artist.setUserid(2);
        artist.setManagerId(3);
        artist.setEmail("artist@test.com");
        artist.setFirstName("Artist");

        UserDetails manager = new UserDetails();
        manager.setUserid(3);
        manager.setEmail("manager@test.com");
        manager.setFirstName("Manager");

        UserDetails admin = new UserDetails();
        admin.setUserid(1);
        admin.setEmail("admin@test.com");
        admin.setFirstName("Admin");

        Partnership partnership = new Partnership();
        partnership.setPercentage(20.0); // 20% manager share

        // Setup mocks
        when(royaltyRepository.findById(1)).thenReturn(Optional.of(royalty));
        when(userDetailsRepository.findById(2)).thenReturn(Optional.of(artist));
        when(userDetailsRepository.findById(3)).thenReturn(Optional.of(manager));
        when(userDetailsRepository.findById(1)).thenReturn(Optional.of(admin));
        when(partnershipRepository.findByArtistIdAndManagerIdAndStatus(2, 3, "Accepted"))
                .thenReturn(Optional.of(partnership));
        when(streamsRepository.findSongIdsByRoyaltyId(1)).thenReturn(Arrays.asList(1, 2));

        // Execute
        royaltyService.processRoyaltyPayment(1, 1);

        // Verify both artist and manager transactions
        verify(transactionService).addTransaction(argThat(transaction -> 
            transaction.getManagerId() == 2 && Math.abs(transaction.getTransactionAmount() - 800.0) < 0.01
        ));
        verify(transactionService).addTransaction(argThat(transaction -> 
            transaction.getManagerId() == 3 && Math.abs(transaction.getTransactionAmount() - 200.0) < 0.01
        ));

        // Verify all emails were sent
        verify(emailService, times(2)).sendPaymentReceivedEmail(anyString(), anyString(), anyDouble());
        verify(emailService, times(2)).sendPaymentSentEmail(anyString(), anyString(), anyDouble(), anyString());

        // Verify royalty status update
        verify(royaltyRepository).save(argThat(r -> "PAID".equals(r.getStatus())));
        verify(streamsRepository).updateStatusBySongIds(anyList(), eq("PROCESSED"));
    }
}