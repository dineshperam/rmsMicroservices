package com.rms.repository;
 
import java.util.List;
 
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
 
import com.rms.model.Royalty;

@Repository
public interface RoyaltyRepository extends JpaRepository<Royalty, Integer> {
	List<Royalty> findByArtistId(int artistId);
	List<Royalty> findBySongId(int songId);
	
	
}