package com.rms.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rms.model.Streams;
import com.rms.service.StreamsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stream")
@CrossOrigin(origins = "*")
public class StreamsController {

    
    private final StreamsService streamService;

    @GetMapping("/streamList")
    public List<Streams> streamList() {
        return streamService.showStream();
    }

    @GetMapping("/searchId/{id}")
    public ResponseEntity<Streams> searchStreamById(@PathVariable int id) {
        return ResponseEntity.ok(streamService.searchStreamById(id));
    }

    @PostMapping("/addStream")
    public ResponseEntity<String> addStream(@RequestBody Streams stream) {
        streamService.addStream(stream);
        return ResponseEntity.ok("Stream added successfully");
    }

    @PutMapping("/updateStream")
    public ResponseEntity<String> updateStream(@RequestBody Streams updatedStream) {
        streamService.updateStream(updatedStream);
        return ResponseEntity.ok("Stream updated successfully");
    }

    @DeleteMapping("/deleteStream/{id}")
    public ResponseEntity<String> deleteStream(@PathVariable int id) {
        streamService.deleteStream(id);
        return ResponseEntity.ok("Stream deleted successfully");
    }

    @GetMapping("/searchSongId/{id}")
    public ResponseEntity<List<Streams>> searchSongById(@PathVariable int id) {
        return ResponseEntity.ok(streamService.searchBysongId(id));
    }

    @GetMapping("/in-progress")
    public List<Streams> getInProgressStreams() {
        return streamService.getInProgressStreams();
    }
}
