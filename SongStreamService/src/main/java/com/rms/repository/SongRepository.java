package com.rms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.rms.model.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
	
	Song findByTitle(String title);
	List<Song> findByArtistId(int artistId);
	

}
