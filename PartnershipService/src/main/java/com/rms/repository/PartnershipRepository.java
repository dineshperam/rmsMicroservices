package com.rms.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rms.model.Partnership;

import jakarta.transaction.Transactional;

@Repository
public interface PartnershipRepository extends JpaRepository<Partnership, Integer> {
	
	Optional<Partnership> findByArtistIdAndManagerId(int artistId, int managerId);
	
    List<Partnership> findByManagerIdAndStatus(int managerId, String status);
    
    Optional<Partnership> findByArtistIdAndStatus(int artistId, String status);
    
    Optional<Partnership> findByArtistIdAndManagerIdAndStatus(int artistId, int managerId, String status);

    @Query("SELECT p FROM Partnership p WHERE p.artistId = :artistId and p.status = 'ACCEPTED' ORDER BY p.startDate DESC LIMIT 1")
    Optional<Partnership> findMostRecentByArtistId(@Param("artistId") int artistId);
    
    @Query("SELECT p FROM Partnership p WHERE p.status = 'PENDING' AND p.startDate < :yesterday")
    List<Partnership> findPendingPartnershipsOlderThan(Date yesterday);

    @Transactional
    @Modifying
    @Query("UPDATE Partnership p SET p.status = 'INACTIVE' WHERE p.status = 'PENDING' AND p.startDate < :yesterday")
    void markOldPendingPartnershipsAsInactive(Date yesterday);


	
}
