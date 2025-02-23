package com.rms.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import com.rms.CONSTANTS;
import com.rms.exeptions.InvalidPartnershipRequestException;
import com.rms.exeptions.NotFoundException;
import com.rms.model.Partnership;
import com.rms.model.UserDetails;
import com.rms.repository.PartnershipRepository;
import com.rms.repository.UserDetailsRepository;

@Service
public class PartnershipService {

    private final PartnershipRepository partnershipRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final EmailService emailService;

    private static final Logger logger = Logger.getLogger(PartnershipService.class);



    public PartnershipService(
        PartnershipRepository partnershipRepository,
        UserDetailsRepository userDetailsRepository,
        EmailService emailService
    ) {
        this.partnershipRepository = partnershipRepository;
        this.userDetailsRepository = userDetailsRepository;
		this.emailService = emailService;
    }

    public Partnership createPartnership(Partnership partnership) {
        logger.info("Creating a new partnership between Artist ID: " + partnership.getArtistId() + 
                    " and Manager ID: " + partnership.getManagerId());
        Partnership savedPartnership = partnershipRepository.save(partnership);
        logger.info("Partnership created with ID: " + savedPartnership.getPartnershipId());
        return savedPartnership;
    }

    public List<Partnership> getAllPartnerships() {
        logger.info("Fetching all partnerships.");
        return partnershipRepository.findAll();
    }

    public Partnership getPartnershipById(int id) {
        logger.info("Searching for partnership with ID: " + id);
        return partnershipRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("No Partnership ID: " + id);
                    return new NotFoundException("No Partnership found with ID: " + id);
                });
    }

    public Optional<Partnership> getPartnershipByArtistId(int artistId) {
        logger.info("Searching for most recent partnership for Artist ID: " + artistId);
        return partnershipRepository.findMostRecentByArtistId(artistId);
    }

    public Partnership updatePartnership(int id, Partnership partnership) {
        logger.info("Updating partnership with ID: " + id);
        Partnership existing = getPartnershipById(id);
        existing.setArtistId(partnership.getArtistId());
        existing.setManagerId(partnership.getManagerId());
        existing.setStatus(partnership.getStatus());
        logger.info("Partnership updated successfully for ID: " + id);
        return partnershipRepository.save(existing);
    }

    public void deletePartnership(int id) {
        logger.info("Attempting to delete partnership with ID: " + id);
        if (!partnershipRepository.existsById(id)) {
            logger.error("Partnership not found with ID: " + id);
            throw new NotFoundException("Partnership not found with ID: " + id);
        }
        partnershipRepository.deleteById(id);
        logger.info("Partnership deleted successfully with ID: " + id);
    }

    public Partnership sendRequest(int artistId, int managerId, Double percentage, int durationMonths, String comments) {
        logger.info("Sending partnership request from Artist ID: " + artistId + " to Manager ID: " + managerId);

        if (durationMonths <= 0) {
            logger.error("Invalid duration: " + durationMonths);
            throw new InvalidPartnershipRequestException("Duration must be greater than 0.");
        }

        if (percentage == null || percentage <= 0) {
            logger.error("Invalid percentage: " + percentage);
            throw new InvalidPartnershipRequestException("Percentage must be greater than zero.");
        }

        Optional<Partnership> existingRequest = partnershipRepository.findByArtistIdAndManagerIdAndStatus(artistId, managerId, CONSTANTS.STATUS_PENDING);
        if (existingRequest.isPresent()) {
            logger.warn("Request already sent to this manager.");
            throw new InvalidPartnershipRequestException("Request already sent to this manager.");
        }

        UserDetails artist = userDetailsRepository.findById(artistId)
                .orElseThrow(() -> new NotFoundException("Artist not found with ID: " + artistId));

        UserDetails manager = userDetailsRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("Manager not found with ID: " + managerId));

        Date startDate = new Date();
        Date endDate = calculateEndDate(startDate, durationMonths);

        Partnership partnership = new Partnership();
        partnership.setArtistId(artistId);
        partnership.setManagerId(managerId);
        partnership.setPercentage(percentage);
        partnership.setDurationMonths(durationMonths);
        partnership.setComments(comments);
        partnership.setStatus(CONSTANTS.STATUS_PENDING);
        partnership.setStartDate(startDate);
        partnership.setEndDate(endDate);

        Partnership savedPartnership = partnershipRepository.save(partnership);

        // **Send email notification to the manager**
        emailService.sendPartnershipRequestEmail(manager.getEmail(), manager.getFirstName(), artist.getFirstName());

        logger.info("Partnership request sent successfully.");
        return savedPartnership;
    }

    public List<Partnership> getRequestsForManager(int managerId) {
        logger.info("Fetching pending partnership requests for Manager ID: " + managerId);
        return partnershipRepository.findByManagerIdAndStatus(managerId, CONSTANTS.STATUS_PENDING);
    }

    public Partnership respondToRequest(int partnershipId, String status) {
        logger.info("Processing response for partnership ID: " + partnershipId + " with status: " + status);

        Partnership partnership = partnershipRepository.findById(partnershipId)
                .orElseThrow(() -> {
                    logger.error("❌ Request not found for partnership ID: " + partnershipId);
                    return new NotFoundException("Request not found");
                });

        UserDetails artist = userDetailsRepository.findById(partnership.getArtistId())
                .orElseThrow(() -> new NotFoundException("Artist not found with ID: " + partnership.getArtistId()));

        UserDetails manager = userDetailsRepository.findById(partnership.getManagerId())
                .orElseThrow(() -> new NotFoundException("Manager not found with ID: " + partnership.getManagerId()));

        boolean isAccepted = false;

        if ("Accepted".equalsIgnoreCase(status)) {
            partnership.setStatus(CONSTANTS.STATUS_ACCEPTED);
            partnership.setEndDate(calculateEndDate(partnership.getStartDate(), partnership.getDurationMonths()));

            logger.info("Partnership accepted. Updating artist-manager relationship.");

            artist.setManagerId(partnership.getManagerId());
            userDetailsRepository.save(artist);

            logger.info("Artist ID: " + artist.getUserid() + " is now linked to Manager ID: " + partnership.getManagerId());
            isAccepted = true;
        } else if ("Rejected".equalsIgnoreCase(status)) {
            partnership.setStatus(CONSTANTS.STATUS_INACTIVE);
            logger.info("Partnership request rejected for ID: " + partnershipId);
        } else {
            logger.error("Invalid status provided: " + status);
            throw new InvalidPartnershipRequestException("Invalid status: " + status);
        }

        Partnership updatedPartnership = partnershipRepository.save(partnership);

        // **Send email notification to the artist**
        emailService.sendPartnershipResponseEmail(artist.getEmail(), artist.getFirstName(), manager.getFirstName(), isAccepted);

        return updatedPartnership;
    }

    private Date calculateEndDate(Date startDate, int durationMonths) {
        logger.info("Calculating end date for partnership.");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MONTH, durationMonths);
        Date endDate = calendar.getTime();
        logger.info("End date calculated: " + endDate);
        return endDate;
    }

    public Optional<Partnership> getPendingRequestForArtist(int artistId) {
        logger.info("Searching for pending request for Artist ID: " + artistId);
        return partnershipRepository.findByArtistIdAndStatus(artistId, CONSTANTS.STATUS_PENDING);
    }
    
    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
    public void markOldPendingPartnershipsAsInactive() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1); // Get yesterday's date
        Date yesterday = calendar.getTime();

        List<Partnership> oldPendingPartnerships = partnershipRepository.findPendingPartnershipsOlderThan(yesterday);

        if (!oldPendingPartnerships.isEmpty()) {
            partnershipRepository.markOldPendingPartnershipsAsInactive(yesterday);
            logger.info("Updated " + oldPendingPartnerships.size() + " pending partnerships to INACTIVE.");
        } else {
            logger.info("No pending partnerships to update.");
        }
    }
}
