package com.rms.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rms.exeptions.InvalidPartnershipRequestException;
import com.rms.exeptions.NotFoundException;
import com.rms.model.Partnership;
import com.rms.service.PartnershipService;
import com.rms.service.PdfService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/partnerships")
@CrossOrigin(origins = "*")
public class PartnershipController {

    
    private final PartnershipService partnershipService;
    
    
    private final PdfService pdfService;
   
    @PostMapping("/add")
    public ResponseEntity<Partnership> createPartnership(@RequestBody Partnership partnership) {
        Partnership createdPartnership = partnershipService.createPartnership(partnership);
        return new ResponseEntity<>(createdPartnership, HttpStatus.CREATED);
    }

    @GetMapping("/showAllPartners")
    public ResponseEntity<List<Partnership>> getAllPartnerships() {
        List<Partnership> partnerships = partnershipService.getAllPartnerships();
        return new ResponseEntity<>(partnerships, HttpStatus.OK);
    }

    @GetMapping("/showbyID/{id}")
    public ResponseEntity<Partnership> getPartnershipById(@PathVariable int id) {
        Partnership partnership = partnershipService.getPartnershipById(id);
        return ResponseEntity.ok(partnership);
    }
    
    @GetMapping("/latest/{artistId}")
    public ResponseEntity<Partnership> getLatestPartnershipByArtistId(@PathVariable int artistId) {
        return partnershipService.getPartnershipByArtistId(artistId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("No partnership found for artist with ID: " + artistId));
    }
    
    @PutMapping("/updatebyId/{id}")
    public ResponseEntity<Partnership> updatePartnership(
            @PathVariable int id, @RequestBody Partnership partnership) {
        Partnership updatedPartnership = partnershipService.updatePartnership(id, partnership);
        return new ResponseEntity<>(updatedPartnership, HttpStatus.OK);
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<String> deletePartnership(@PathVariable int id) {
        partnershipService.deletePartnership(id);
        return ResponseEntity.ok("Partnership deleted successfully!");
    }
    
    @GetMapping("/export-pdf-partner/{artistId}")
    public ResponseEntity<byte[]> exportPartnershipsToPdf(@PathVariable int artistId) {
        Optional<Partnership> partnership = partnershipService.getPartnershipByArtistId(artistId);
        if (partnership.isEmpty()) {
            throw new NotFoundException("No partnership found for artist with ID: " + artistId);
        }
        byte[] pdfBytes = pdfService.generatePartnershipPdf(partnership);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=partnerships.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/request")
    public ResponseEntity<Partnership> sendRequest(@RequestBody Map<String, Object> requestMap) {
        try {
            Integer artistId = requestMap.get("artistId") instanceof Number number ? number.intValue() : null;
            Integer managerId = requestMap.get("managerId") instanceof Number number ? number.intValue() : null;
            Double percentage = requestMap.get("percentage") instanceof Number number ? number.doubleValue() : null;
            Integer durationMonths = requestMap.get("durationMonths") instanceof Number number ? number.intValue() : null;
            String comments = (String) requestMap.getOrDefault("comments", "");

            if (artistId == null || managerId == null || percentage == null || durationMonths == null) {
                throw new InvalidPartnershipRequestException("Missing required fields.");
            }

            Partnership partnership = partnershipService.sendRequest(artistId, managerId, percentage, durationMonths, comments);
            return ResponseEntity.ok(partnership);
        } catch (InvalidPartnershipRequestException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @GetMapping("/requests/{managerId}")
    public ResponseEntity<List<Partnership>> getRequestsForManager(@PathVariable int managerId) {
        return ResponseEntity.ok(partnershipService.getRequestsForManager(managerId));
    }

    @PutMapping("/respond/{partnershipId}")
    public ResponseEntity<String> respondToRequest(@PathVariable int partnershipId, @RequestParam String status) {
        Partnership updatedPartnership = partnershipService.respondToRequest(partnershipId, status);
        if ("ACCEPTED".equalsIgnoreCase(updatedPartnership.getStatus())) {
            return ResponseEntity.ok("Partnership request accepted successfully!");
        } else if ("INACTIVE".equalsIgnoreCase(updatedPartnership.getStatus())) {
            return ResponseEntity.ok("Partnership request rejected.");
        } else {
            throw new InvalidPartnershipRequestException("Invalid status: " + status);
        }
    }
    
    @GetMapping("/pending/{artistId}")
    public ResponseEntity<Partnership> getPendingPartnershipRequest(@PathVariable int artistId) {
        return partnershipService.getPendingRequestForArtist(artistId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("No pending request found for artist ID: " + artistId));
    }
}
