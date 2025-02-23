package com.rms.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;

@Service
public class RoyaltyService {
    private static final Logger logger = Logger.getLogger(RoyaltyService.class);

    private final RoyaltyRepository royaltyRepository;
    private final TransactionService transactionService;
    private final UserDetailsRepository userDetailsRepository;
    private final StreamsRepository streamsRepository;
    private final PartnershipRepository partnershipRepository;
    private final EmailService emailService;

    public RoyaltyService(RoyaltyRepository royaltyRepository,
                          TransactionService transactionService,
                          UserDetailsRepository userDetailsRepository,
                          StreamsRepository streamsRepository,
                          PartnershipRepository partnershipRepository,
                          EmailService emailService) {
        this.royaltyRepository = royaltyRepository;
        this.transactionService = transactionService;
        this.userDetailsRepository = userDetailsRepository;
        this.streamsRepository = streamsRepository;
        this.partnershipRepository = partnershipRepository;
        this.emailService = emailService;
    }

    public List<Royalty> showRoyalty() {
        logger.info("Fetching all royalties");
        return royaltyRepository.findAll();
    }

    public Royalty searchRoyaltyById(int royaltyId) {
        logger.info("Searching for royalty with ID: " + royaltyId);
        return royaltyRepository.findById(royaltyId)
                .orElseThrow(() -> new NotFoundException("Royalty not found with ID: " + royaltyId));
    }

    public void addRoyalty(Royalty royalty) {
        logger.info("Adding new royalty: " + royalty);
        royaltyRepository.save(royalty);
    }

    public void updateRoyalty(Royalty updatedRoyalty) {
        logger.info("Updating royalty with ID: " + updatedRoyalty.getRoyaltyId());
        royaltyRepository.save(updatedRoyalty);
    }

    public List<Royalty> searchByartistId(int artistId) {
        logger.info("Searching royalties for artist ID: " + artistId);
        List<Royalty> royalties = royaltyRepository.findByArtistId(artistId);
        if (royalties.isEmpty()) {
            logger.warn("No royalties found for artist ID: " + artistId);
            throw new NotFoundException("No royalties found for artist ID: " + artistId);
        }
        return royalties;
    }

    public List<Royalty> searchBysongId(int songId) {
        logger.info("Searching royalties for song ID: " + songId);
        List<Royalty> royalties = royaltyRepository.findBySongId(songId);
        if (royalties.isEmpty()) {
            logger.warn("No royalties found for song ID: " + songId);
            throw new NotFoundException("No royalties found for song ID: " + songId);
        }
        return royalties;
    }

    public double calculateRoyaltyAmount(long streams) {
        logger.info("Calculating royalty for streams: " + streams);
        if (streams <= 10000) {
            return streams * 0.002;
        } else if (streams <= 50000) {
            return 10000 * 0.002 + (streams - 10000) * 0.005;
        } else {
            return 10000 * 0.002 + (50000 - 10000) * 0.005 + (streams - 50000) * 0.01;
        }
    }

    @Transactional
    public void calculateAndStoreRoyalty() {
        logger.info("Starting royalty calculation and storage process...");
        List<Streams> streamsList = streamsRepository.findByStatus("IN PROGRESS");

        for (Streams stream : streamsList) {
            int songId = stream.getSongId();
            int artistId = stream.getUserId();
            long streamCount = stream.getStreamCount();

            double royaltyAmount = calculateRoyaltyAmount(streamCount);

            logger.info("Calculated royalty for Song ID: " + songId + ", Artist ID: " + artistId +
                    ", Streams: " + streamCount + ", Royalty Amount: " + royaltyAmount);

            Royalty royalty = new Royalty();
            royalty.setSongId(songId);
            royalty.setArtistId(artistId);
            royalty.setTotalStreams(streamCount);
            royalty.setRoyaltyAmount(royaltyAmount);
            royalty.setCalculatedDate(new Date());
            royalty.setStatus("PENDING");

            royaltyRepository.save(royalty);
        }
        logger.info("Royalty calculation and storage process completed.");
    }

    @Transactional
    public void processRoyaltyPayment(int royaltyId, int adminId) {
        logger.info("Processing royalty payment for ID: " + royaltyId);

        Royalty royalty = royaltyRepository.findById(royaltyId)
                .orElseThrow(() -> new NotFoundException("Royalty not found with ID: " + royaltyId));

        UserDetails artist = userDetailsRepository.findById(royalty.getArtistId())
                .orElseThrow(() -> new NotFoundException("Artist not found with ID: " + royalty.getArtistId()));

        int managerId = artist.getManagerId();
        double totalAmount = royalty.getRoyaltyAmount();

        logger.info("Total royalty amount: " + totalAmount + " for Artist ID: " + artist.getUserid());

        double managerSharePercentage = partnershipRepository
                .findByArtistIdAndManagerIdAndStatus(artist.getUserid(), managerId, "Accepted")
                .map(Partnership::getPercentage)
                .orElse(0.0);

        double managerShare = (managerSharePercentage / 100) * totalAmount;
        double artistShare = totalAmount - managerShare;
        
        logger.info("Artist share: " + artistShare + ", Manager share: " + managerShare);

        UserDetails admin = userDetailsRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin not found with ID: " + adminId));

        Transactions artistTransaction = new Transactions(
                artist.getUserid(), adminId, royaltyId, new Date(), artistShare, artist.getManagerId(), "CREDIT"
        );
        transactionService.addTransaction(artistTransaction);

        logger.info("Transaction recorded: Artist ID: " + artist.getUserid() + ", Amount: " + artistShare);

        emailService.sendPaymentReceivedEmail(artist.getEmail(), artist.getFirstName(), artistShare);
        emailService.sendPaymentSentEmail(admin.getEmail(), admin.getFirstName(), artistShare, artist.getFirstName());

        if (managerShare > 0) {
            UserDetails manager = userDetailsRepository.findById(managerId)
                    .orElseThrow(() -> new NotFoundException("Manager not found with ID: " + managerId));

            Transactions managerTransaction = new Transactions(
                    manager.getUserid(), adminId, royaltyId, new Date(), managerShare, manager.getUserid(), "CREDIT"
            );
            transactionService.addTransaction(managerTransaction);
 
            logger.info("Transaction recorded: Manager ID: " + manager.getUserid() + ", Amount: " + managerShare);

            emailService.sendPaymentReceivedEmail(manager.getEmail(), manager.getFirstName(), managerShare);
            emailService.sendPaymentSentEmail(admin.getEmail(), admin.getFirstName(), managerShare, manager.getFirstName());
        }
 
        royalty.setStatus("PAID");
        royaltyRepository.save(royalty);

        logger.info("Royalty payment processed successfully for ID: " + royaltyId);

        List<Integer> songIds = streamsRepository.findSongIdsByRoyaltyId(royaltyId);
        if (!songIds.isEmpty()) {
            logger.info("Updating stream statuses to PROCESSED for Royalty ID: " + royaltyId);
            streamsRepository.updateStatusBySongIds(songIds, "PROCESSED");
        }
    }
}
