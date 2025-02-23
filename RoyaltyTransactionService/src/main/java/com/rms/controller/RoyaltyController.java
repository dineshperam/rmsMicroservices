package com.rms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rms.model.Royalty;
import com.rms.service.RoyaltyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/royalty")
@CrossOrigin(origins="*")
public class RoyaltyController {

    
    private final RoyaltyService royaltyService;

    @GetMapping("/royaltyList")
    public ResponseEntity<List<Royalty>> getAllRoyalties() {
        return ResponseEntity.ok(royaltyService.showRoyalty());
    }

    @GetMapping("/searchId/{id}")
    public ResponseEntity<Royalty> searchRoyaltyById(@PathVariable int id) {
        return ResponseEntity.ok(royaltyService.searchRoyaltyById(id));
    }

    @PostMapping("/addRoyalty")
    public ResponseEntity<String> addRoyalty(@RequestBody Royalty royalty) {
        royaltyService.addRoyalty(royalty);
        return ResponseEntity.status(HttpStatus.CREATED).body("Royalty added successfully.");
    }

    @PutMapping("/updateRoyalty")
    public ResponseEntity<String> updateRoyalty(@RequestBody Royalty updatedRoyalty) {
        royaltyService.updateRoyalty(updatedRoyalty);
        return ResponseEntity.ok("Royalty updated successfully.");
    }

    @PutMapping("/payRoyalty/{id}/{adminId}")
    public ResponseEntity<Map<String, String>> payRoyalty(@PathVariable int id, @PathVariable int adminId) {
        royaltyService.processRoyaltyPayment(id, adminId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Royalty payment processed successfully.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/calculate")
    public ResponseEntity<String> calculateRoyalty() {
        royaltyService.calculateAndStoreRoyalty();
        return ResponseEntity.ok("Royalty calculation completed successfully.");
    }
}
