package com.rms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rms.model.Partnership;

public interface PartnershipRepository extends JpaRepository<Partnership, Integer> {
	
	Optional<Partnership> findByArtistIdAndManagerIdAndStatus(int artistId, int managerId, String status);

}
