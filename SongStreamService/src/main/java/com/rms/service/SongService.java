package com.rms.service;

import org.apache.log4j.Logger;
import java.util.List;
import org.springframework.stereotype.Service;

import com.rms.CONSTANTS;
import com.rms.exeptions.NotFoundException;
import com.rms.model.Song;
import com.rms.repository.SongRepository;

@Service
public class SongService {
    
    private static final Logger logger = Logger.getLogger(SongService.class);
    
    

    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public List<Song> songsList() {
        logger.info("Fetching all songs.");
        return songRepository.findAll();
    }

    public Song searchSongById(int songId) {
        logger.info("Searching for song with ID: " + songId);
        return songRepository.findById(songId)
                .orElseThrow(() -> {
                    logger.warn("Song with ID " + songId + CONSTANTS.NOT_FOUND_MSG);
                    return new NotFoundException("Song with ID " + songId + CONSTANTS.NOT_FOUND_MSG);
                });
    }

    public void addSong(Song song) {
        if (song == null) {
            throw new IllegalArgumentException("Song cannot be null");
        }
        if (song.getTitle() == null || song.getTitle().isEmpty()) {
            logger.warn("Attempt to add a song with an empty title.");
            throw new IllegalArgumentException("Song title cannot be empty");
        }
        logger.info("Adding new song: " + song.getTitle());
        songRepository.save(song);
    }

    public void updateSong(Song updatedSong) {
        if (updatedSong == null) {
            throw new IllegalArgumentException("Updated song cannot be null");
        }
        if (!songRepository.existsById(updatedSong.getSongId())) {
            logger.warn("Cannot update, song with ID " + updatedSong.getSongId() + CONSTANTS.NOT_FOUND_MSG);
            throw new NotFoundException("Cannot update, song with ID " + updatedSong.getSongId() + CONSTANTS.NOT_FOUND_MSG);
        }
        songRepository.save(updatedSong);
    }

    public void deleteSong(int id) {
        logger.info("Deleting song with ID: " + id);
        if (!songRepository.existsById(id)) {
            logger.warn("Cannot delete, song with ID " + id + " not found.");
            throw new NotFoundException("Cannot delete, song with ID " + id + CONSTANTS.NOT_FOUND_MSG);
        }
        songRepository.deleteById(id);
    }

    public Song searchByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        
        logger.info("Searching for song with title: " + title);
        Song song = songRepository.findByTitle(title);

        if (song == null) {
            String errorMessage = "Song with title '" + title + "' " + CONSTANTS.NOT_FOUND_MSG;
            logger.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        return song;
    }


    public List<Song> searchByArtistId(int id) {
        logger.info("Fetching songs for artist with ID: " + id);
        List<Song> songs = songRepository.findByArtistId(id);
        if (songs.isEmpty()) {
            logger.warn("No songs found for artist with ID " + id);
            throw new NotFoundException("No songs found for artist with ID " + id);
        }
        return songs;
    }
}
