package com.rms.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rms.CONSTANTS;
import com.rms.exeptions.InvalidPartnershipRequestException;
import com.rms.exeptions.NotFoundException;
import com.rms.model.Partnership;
import com.rms.model.UserDetails;
import com.rms.repository.PartnershipRepository;
import com.rms.repository.UserDetailsRepository;
import com.rms.service.EmailService;
import com.rms.service.PartnershipService;

@ExtendWith(MockitoExtension.class)
class PartnershipServiceTest {

    @Mock
    private PartnershipRepository partnershipRepository;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PartnershipService partnershipService;
    private Partnership partnership1;
    private UserDetails artist1;
    private UserDetails manager;

    @BeforeEach
    void setUp() {
        partnership1 = new Partnership();
        partnership1.setPartnershipId(1);
        partnership1.setArtistId(10);
        partnership1.setManagerId(20);
        partnership1.setPercentage(50.0);
        partnership1.setDurationMonths(6);
        partnership1.setStatus(CONSTANTS.STATUS_PENDING);
        partnership1.setStartDate(new Date());

        artist1 = new UserDetails();
        artist1.setUserid(10);
        artist1.setFirstName("John");
        artist1.setEmail("artist@example.com");

        manager = new UserDetails();
        manager.setUserid(20);
        manager.setFirstName("Mike");
        manager.setEmail("manager@example.com");
    }
    
    @Test
    void testCreatePartnership_Success() {
        when(partnershipRepository.save(partnership1)).thenReturn(partnership1);

        Partnership savedPartnership = partnershipService.createPartnership(partnership1);

        assertNotNull(savedPartnership);
        assertEquals(1, savedPartnership.getPartnershipId());
        verify(partnershipRepository).save(partnership1);
    }
    @Test
    void testGetAllPartnerships_Success() {
        when(partnershipRepository.findAll()).thenReturn(List.of(partnership1));

        List<Partnership> result = partnershipService.getAllPartnerships();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(partnershipRepository).findAll();
    }
    @Test
    void testGetPartnershipById_Success() {
        when(partnershipRepository.findById(1)).thenReturn(Optional.of(partnership1));

        Partnership result = partnershipService.getPartnershipById(1);

        assertNotNull(result);
        assertEquals(10, result.getArtistId());
        verify(partnershipRepository).findById(1);
    }
    @Test
    void testGetPartnershipById_NotFound() {
        when(partnershipRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> partnershipService.getPartnershipById(1));
        verify(partnershipRepository).findById(1);
    }
    @Test
    void testUpdatePartnership_Success() {
        when(partnershipRepository.findById(1)).thenReturn(Optional.of(partnership1));
        when(partnershipRepository.save(any(Partnership.class))).thenReturn(partnership1);

        Partnership updated = partnershipService.updatePartnership(1, partnership1);

        assertNotNull(updated);
        assertEquals(10, updated.getArtistId());
        verify(partnershipRepository).save(any(Partnership.class));
    }
    @Test
    void testUpdatePartnership_NotFound() {
        when(partnershipRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> partnershipService.updatePartnership(1, partnership1));
    }
    @Test
    void testDeletePartnership_Success() {
        when(partnershipRepository.existsById(1)).thenReturn(true);

        partnershipService.deletePartnership(1);

        verify(partnershipRepository).deleteById(1);
    }
    @Test
    void testDeletePartnership_NotFound() {
        when(partnershipRepository.existsById(1)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> partnershipService.deletePartnership(1));
    }
    @Test
    void testGetPartnershipByArtistId_Found() {
        int artistId = 10;
        Partnership mockPartnership = new Partnership();
        mockPartnership.setArtistId(artistId);
        mockPartnership.setManagerId(20);
        mockPartnership.setStatus(CONSTANTS.STATUS_ACCEPTED);

        when(partnershipRepository.findMostRecentByArtistId(artistId)).thenReturn(Optional.of(mockPartnership));

        Optional<Partnership> result = partnershipService.getPartnershipByArtistId(artistId);

        assertTrue(result.isPresent());
        assertEquals(artistId, result.get().getArtistId());
        verify(partnershipRepository, times(1)).findMostRecentByArtistId(artistId);
    }

    @Test
    void testGetPartnershipByArtistId_NotFound() {
        int artistId = 10;

        when(partnershipRepository.findMostRecentByArtistId(artistId)).thenReturn(Optional.empty());

        Optional<Partnership> result = partnershipService.getPartnershipByArtistId(artistId);

        assertFalse(result.isPresent());
        verify(partnershipRepository, times(1)).findMostRecentByArtistId(artistId);
    }
    @Test
    void testGetRequestsForManager_WithRequests() {
        int managerId = 20;
        Partnership partnership1 = new Partnership();
        partnership1.setArtistId(10);
        partnership1.setManagerId(managerId);
        partnership1.setStatus(CONSTANTS.STATUS_PENDING);

        Partnership partnership2 = new Partnership();
        partnership2.setArtistId(11);
        partnership2.setManagerId(managerId);
        partnership2.setStatus(CONSTANTS.STATUS_PENDING);

        List<Partnership> mockRequests = Arrays.asList(partnership1, partnership2);

        when(partnershipRepository.findByManagerIdAndStatus(managerId, CONSTANTS.STATUS_PENDING))
            .thenReturn(mockRequests);

        List<Partnership> result = partnershipService.getRequestsForManager(managerId);

        assertEquals(2, result.size());
        assertEquals(managerId, result.get(0).getManagerId());
        assertEquals(CONSTANTS.STATUS_PENDING, result.get(0).getStatus());

        verify(partnershipRepository, times(1)).findByManagerIdAndStatus(managerId, CONSTANTS.STATUS_PENDING);
    }

    @Test
    void testGetRequestsForManager_NoRequests() {
        int managerId = 20;

        when(partnershipRepository.findByManagerIdAndStatus(managerId, CONSTANTS.STATUS_PENDING))
            .thenReturn(Collections.emptyList());

        List<Partnership> result = partnershipService.getRequestsForManager(managerId);

        assertTrue(result.isEmpty());
        verify(partnershipRepository, times(1)).findByManagerIdAndStatus(managerId, CONSTANTS.STATUS_PENDING);
    }


    @Test
    void testSendRequest_Success() {
        int artistId = 10;
        int managerId = 20;
        double percentage = 50.0;
        int durationMonths = 6;
        String comments = "Test Comments";

        when(partnershipRepository.findByArtistIdAndManagerIdAndStatus(artistId, managerId, CONSTANTS.STATUS_PENDING))
            .thenReturn(Optional.empty());
        when(userDetailsRepository.findById(artistId)).thenReturn(Optional.of(artist1));
        when(userDetailsRepository.findById(managerId)).thenReturn(Optional.of(manager));
        when(partnershipRepository.save(any(Partnership.class))).thenReturn(partnership1);

        Partnership result = partnershipService.sendRequest(artistId, managerId, percentage, durationMonths, comments);

        assertNotNull(result);
        verify(emailService).sendPartnershipRequestEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testSendRequest_AlreadyExists() {
        int artistId = 10;
        int managerId = 20;
        double percentage = 50.0;
        int durationMonths = 6;
        String comments = "Test Comments";

        // Mock existing partnership to simulate a duplicate request
        Partnership existingPartnership = new Partnership();
        existingPartnership.setArtistId(artistId);
        existingPartnership.setManagerId(managerId);
        existingPartnership.setStatus(CONSTANTS.STATUS_PENDING);

        when(partnershipRepository.findByArtistIdAndManagerIdAndStatus(artistId, managerId, CONSTANTS.STATUS_PENDING))
            .thenReturn(Optional.of(existingPartnership));

        // Ensure that the method throws the expected exception
        InvalidPartnershipRequestException exception = assertThrows(
        	    InvalidPartnershipRequestException.class,
        	    () -> partnershipService.sendRequest(artistId, managerId, percentage, durationMonths, comments)
        	);
        assertEquals("Request already sent to this manager.", exception.getMessage());

        // Verify that no further calls were made (Artist & Manager should NOT be fetched)
        verify(userDetailsRepository, never()).findById(anyInt());
        verify(partnershipRepository, never()).save(any(Partnership.class));
        verify(emailService, never()).sendPartnershipRequestEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testSendRequest_InvalidDuration() {
        int artistId = 10;
        int managerId = 20;
        double percentage = 50.0;
        int durationMonths = 0; // Invalid duration
        String comments = "Test Comments";

        InvalidPartnershipRequestException exception = assertThrows(
        	    InvalidPartnershipRequestException.class,
        	    () -> partnershipService.sendRequest(artistId, managerId, percentage, durationMonths, comments)
        	);

        assertEquals("Duration must be greater than 0.", exception.getMessage());

        // Ensure no database calls were made (since validation failed early)
        verify(partnershipRepository, never()).findByArtistIdAndManagerIdAndStatus(anyInt(), anyInt(), anyString());
        verify(userDetailsRepository, never()).findById(anyInt());
        verify(partnershipRepository, never()).save(any(Partnership.class));
        verify(emailService, never()).sendPartnershipRequestEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testSendRequest_InvalidPercentage_Zero() {
        int artistId = 10;
        int managerId = 20;
        Double percentage = 0.0; // Invalid percentage
        int durationMonths = 6;
        String comments = "Test Comments";

        InvalidPartnershipRequestException exception = assertThrows(
        	    InvalidPartnershipRequestException.class,
        	    () -> partnershipService.sendRequest(artistId, managerId, percentage, durationMonths, comments)
        	);

        assertEquals("Percentage must be greater than zero.", exception.getMessage());

        // Ensure no database calls were made (since validation failed early)
        verifyNoInteractions(partnershipRepository, userDetailsRepository, emailService);
    }

    @Test
    void testSendRequest_InvalidPercentage_Null() {
        int artistId = 10;
        int managerId = 20;
        Double percentage = null; // Null case
        int durationMonths = 6;
        String comments = "Test Comments";

        InvalidPartnershipRequestException exception = assertThrows(
        	    InvalidPartnershipRequestException.class,
        	    () -> partnershipService.sendRequest(artistId, managerId, percentage, durationMonths, comments)
        	);
        assertEquals("Percentage must be greater than zero.", exception.getMessage());

        // Ensure no database calls were made (since validation failed early)
        verifyNoInteractions(partnershipRepository, userDetailsRepository, emailService);
    }


    @Test
    void testSendRequest_ArtistNotFound() {
        int artistId = 10;
        int managerId = 20;
        double percentage = 50.0;
        int durationMonths = 6;
        String comments = "Test Comments";

        when(partnershipRepository.findByArtistIdAndManagerIdAndStatus(artistId, managerId, CONSTANTS.STATUS_PENDING))
            .thenReturn(Optional.empty());
        when(userDetailsRepository.findById(artistId)).thenReturn(Optional.empty()); // Simulate artist not found

        NotFoundException exception = assertThrows(
        	    NotFoundException.class,
        	    () -> partnershipService.sendRequest(artistId, managerId, percentage, durationMonths, comments)
        	);

        assertEquals("Artist not found with ID: " + artistId, exception.getMessage());

        // Ensure the manager lookup & save were **never called**
        verify(userDetailsRepository, never()).findById(managerId);
        verify(partnershipRepository, never()).save(any(Partnership.class));
        verify(emailService, never()).sendPartnershipRequestEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testSendRequest_ManagerNotFound() {
        int artistId = 10;
        int managerId = 20;
        double percentage = 50.0;
        int durationMonths = 6;
        String comments = "Test Comments";

        when(partnershipRepository.findByArtistIdAndManagerIdAndStatus(artistId, managerId, CONSTANTS.STATUS_PENDING))
            .thenReturn(Optional.empty());
        when(userDetailsRepository.findById(artistId)).thenReturn(Optional.of(new UserDetails())); // Artist exists
        when(userDetailsRepository.findById(managerId)).thenReturn(Optional.empty()); // Manager not found

        NotFoundException exception = assertThrows(
        	    NotFoundException.class,
        	    () -> partnershipService.sendRequest(artistId, managerId, percentage, durationMonths, comments)
        	);
        assertEquals("Manager not found with ID: " + managerId, exception.getMessage());

        // Ensure partnership save & email were **never called**
        verify(partnershipRepository, never()).save(any(Partnership.class));
        verify(emailService, never()).sendPartnershipRequestEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testGetPendingRequestForArtist_Found() {
        int artistId = 10;
        Partnership mockPartnership = new Partnership();
        mockPartnership.setArtistId(artistId);
        mockPartnership.setManagerId(20);
        mockPartnership.setStatus(CONSTANTS.STATUS_PENDING);

        when(partnershipRepository.findByArtistIdAndStatus(artistId, CONSTANTS.STATUS_PENDING))
            .thenReturn(Optional.of(mockPartnership));

        Optional<Partnership> result = partnershipService.getPendingRequestForArtist(artistId);

        assertTrue(result.isPresent());
        assertEquals(artistId, result.get().getArtistId());
        assertEquals(CONSTANTS.STATUS_PENDING, result.get().getStatus());

        verify(partnershipRepository, times(1)).findByArtistIdAndStatus(artistId, CONSTANTS.STATUS_PENDING);
    }

    @Test
    void testGetPendingRequestForArtist_NotFound() {
        int artistId = 10;

        when(partnershipRepository.findByArtistIdAndStatus(artistId, CONSTANTS.STATUS_PENDING))
            .thenReturn(Optional.empty());

        Optional<Partnership> result = partnershipService.getPendingRequestForArtist(artistId);

        assertFalse(result.isPresent());
        verify(partnershipRepository, times(1)).findByArtistIdAndStatus(artistId, CONSTANTS.STATUS_PENDING);
    }


    @Test
    void testRespondToRequest_Accept() {
        when(partnershipRepository.findById(1)).thenReturn(Optional.of(partnership1));
        when(userDetailsRepository.findById(10)).thenReturn(Optional.of(artist1));
        when(userDetailsRepository.findById(20)).thenReturn(Optional.of(manager));
        when(partnershipRepository.save(any(Partnership.class))).thenReturn(partnership1);

        Partnership result = partnershipService.respondToRequest(1, "Accepted");

        assertEquals(CONSTANTS.STATUS_ACCEPTED, result.getStatus());
        verify(emailService).sendPartnershipResponseEmail(anyString(), anyString(), anyString(), eq(true));
    }
    @Test
    void testRespondToRequest_Reject() {
        when(partnershipRepository.findById(1)).thenReturn(Optional.of(partnership1));
        when(userDetailsRepository.findById(10)).thenReturn(Optional.of(artist1));
        when(userDetailsRepository.findById(20)).thenReturn(Optional.of(manager));
        when(partnershipRepository.save(any(Partnership.class))).thenReturn(partnership1);

        Partnership result = partnershipService.respondToRequest(1, "Rejected");

        assertEquals(CONSTANTS.STATUS_INACTIVE, result.getStatus());
        verify(emailService).sendPartnershipResponseEmail(anyString(), anyString(), anyString(), eq(false));
    }
    @Test
    void testRespondToRequest_RequestNotFound() {
        int partnershipId = 100;
        String status = "Accepted";

        when(partnershipRepository.findById(partnershipId)).thenReturn(Optional.empty());

        System.out.println("Mocked partnershipRepository.findById() to return empty.");

        NotFoundException exception = assertThrows(
        	    NotFoundException.class,
        	    () -> partnershipService.respondToRequest(partnershipId, status)
        	);
        System.out.println("Caught exception: " + exception.getMessage());

        assertEquals("Request not found", exception.getMessage());
    }

    @Test
    void testRespondToRequest_InvalidStatus() {
        int partnershipId = 100;
        String status = "UnknownStatus"; // Invalid status

        // Mock partnership existence
        Partnership mockPartnership = new Partnership();
        mockPartnership.setArtistId(10);
        mockPartnership.setManagerId(20);
        mockPartnership.setStatus(CONSTANTS.STATUS_PENDING);

        UserDetails mockArtist = new UserDetails();
        mockArtist.setUserid(10);
        mockArtist.setEmail("artist@example.com");
        mockArtist.setFirstName("Artist");

        UserDetails mockManager = new UserDetails();
        mockManager.setUserid(20);
        mockManager.setEmail("manager@example.com");
        mockManager.setFirstName("Manager");

        when(partnershipRepository.findById(partnershipId)).thenReturn(Optional.of(mockPartnership));
        when(userDetailsRepository.findById(10)).thenReturn(Optional.of(mockArtist));
        when(userDetailsRepository.findById(20)).thenReturn(Optional.of(mockManager));

        // Ensure InvalidPartnershipRequestException is thrown
        InvalidPartnershipRequestException exception = assertThrows(
        	    InvalidPartnershipRequestException.class,
        	    () -> partnershipService.respondToRequest(partnershipId, status)
        	);

        assertEquals("Invalid status: " + status, exception.getMessage());

        // Verify that the partnership was not saved
        verify(partnershipRepository, never()).save(any(Partnership.class));
        verify(emailService, never()).sendPartnershipResponseEmail(anyString(), anyString(), anyString(), anyBoolean());
    }
    @Test
    void testRespondToRequest_ArtistNotFound() {
        int partnershipId = 100;
        String status = "Accepted";

        Partnership partnership = new Partnership();
        partnership.setArtistId(10);
        partnership.setManagerId(20);

        when(partnershipRepository.findById(partnershipId)).thenReturn(Optional.of(partnership));
        when(userDetailsRepository.findById(partnership.getArtistId())).thenReturn(Optional.empty()); // Artist not found

        NotFoundException exception = assertThrows(
        	    NotFoundException.class,
        	    () -> partnershipService.respondToRequest(partnershipId, status)
        	);
        assertEquals("Artist not found with ID: " + partnership.getArtistId(), exception.getMessage());

        // Ensure no further processing occurs
        verify(userDetailsRepository, never()).findById(partnership.getManagerId());
        verify(partnershipRepository, never()).save(any(Partnership.class));
        verify(emailService, never()).sendPartnershipResponseEmail(anyString(), anyString(), anyString(), anyBoolean());
    }

    @Test
    void testRespondToRequest_ManagerNotFound() {
        int partnershipId = 100;
        String status = "Accepted";

        Partnership partnership = new Partnership();
        partnership.setArtistId(10);
        partnership.setManagerId(20);

        UserDetails artist = new UserDetails();
        artist.setUserid(10);

        when(partnershipRepository.findById(partnershipId)).thenReturn(Optional.of(partnership));
        when(userDetailsRepository.findById(partnership.getArtistId())).thenReturn(Optional.of(artist));
        when(userDetailsRepository.findById(partnership.getManagerId())).thenReturn(Optional.empty()); // Manager not found

        NotFoundException exception = assertThrows(
        	    NotFoundException.class,
        	    () -> partnershipService.respondToRequest(partnershipId, status)
        	);

        assertEquals("Manager not found with ID: " + partnership.getManagerId(), exception.getMessage());

        // Ensure no further processing occurs
        verify(partnershipRepository, never()).save(any(Partnership.class));
        verify(emailService, never()).sendPartnershipResponseEmail(anyString(), anyString(), anyString(), anyBoolean());
    }


    @Test
    void testMarkOldPendingPartnershipsAsInactive_WithPendingPartnerships() {
        when(partnershipRepository.findPendingPartnershipsOlderThan(any(Date.class)))
            .thenReturn(List.of(partnership1));

        partnershipService.markOldPendingPartnershipsAsInactive();

        // Verify repository method is called
        verify(partnershipRepository).markOldPendingPartnershipsAsInactive(any(Date.class));

        // Removed `verify(logger).info(...)` (since logger is not a mock)
    }

    @Test
    void testMarkOldPendingPartnershipsAsInactive_NoPendingPartnerships() {
        when(partnershipRepository.findPendingPartnershipsOlderThan(any(Date.class)))
            .thenReturn(Collections.emptyList()); // No partnerships

        partnershipService.markOldPendingPartnershipsAsInactive();

        // Verify that markOldPendingPartnershipsAsInactive is NEVER called
        verify(partnershipRepository, never()).markOldPendingPartnershipsAsInactive(any(Date.class));

        // Removed `verify(logger).info(...)` (since logger is not a mock)
    }



}
