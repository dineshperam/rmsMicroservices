package com.rms.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rms.model.Streams;

import jakarta.transaction.Transactional;

public interface StreamsRepository extends JpaRepository<Streams, Integer> {
 
	
	List<Streams> findByStatus(String status);
	
    
 // First, fetch all songIds related to the given royaltyId
    @Query("SELECT r.songId FROM Royalty r WHERE r.id = :royaltyId")
    List<Integer> findSongIdsByRoyaltyId(@Param("royaltyId") int royaltyId);
    
    // Then, update the streams where the songId matches
    @Transactional
    @Modifying
    @Query("UPDATE Streams s SET s.status = :status WHERE s.songId IN :songIds")
    void updateStatusBySongIds(@Param("songIds") List<Integer> songIds, @Param("status") String status);
}