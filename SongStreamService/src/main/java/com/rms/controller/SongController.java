package com.rms.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rms.model.Song;
import com.rms.service.SongService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/artist")
@CrossOrigin(origins="*")
public class SongController {

    
    private final SongService songService;

    @GetMapping("/songsList")
    public List<Song> songsList() {
        return songService.songsList();
    }

    @GetMapping("/searchId/{id}")
    public ResponseEntity<Song> searchSongById(@PathVariable int id) {
        return ResponseEntity.ok(songService.searchSongById(id));
    }

    @PostMapping("/addSong")
    public ResponseEntity<String> addSong(@RequestBody Song song) {
        songService.addSong(song);
        return ResponseEntity.ok("Song added successfully");
    }

    @PutMapping("/updateSong")
    public ResponseEntity<String> updateSong(@RequestBody Song updatedSong) {
        songService.updateSong(updatedSong);
        return ResponseEntity.ok("Song updated successfully");
    }


    @GetMapping("/searchTitle/{title}")
    public ResponseEntity<Song> searchSongByTitle(@PathVariable String title) {
        return ResponseEntity.ok(songService.searchByTitle(title));
    }

    @GetMapping("/searchByArtistId/{id}")
    public ResponseEntity<List<Song>> searchByArtistId(@PathVariable int id) {
        return ResponseEntity.ok(songService.searchByArtistId(id));
    }
}
