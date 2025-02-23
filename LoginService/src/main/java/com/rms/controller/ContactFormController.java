package com.rms.controller;

import com.rms.exeptions.DuplicateEmailException;
import com.rms.model.ContactForm;
import com.rms.service.ContactFormService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*")
public class ContactFormController {

    
    private final ContactFormService contactFormService;

    @PostMapping("/submit")
    public ResponseEntity<Object> submitContactForm(@RequestBody ContactForm contactForm) {
        try {
            ContactForm savedForm = contactFormService.saveContactForm(contactForm);
            return ResponseEntity.ok(savedForm);
        } catch (DuplicateEmailException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    
    @GetMapping("/showContacts")
    public List<ContactForm> showContacts(){
    	return contactFormService.showContacts();
    }
    
    @PutMapping("/accept/{contactId}/{adminId}")
    public ResponseEntity<String> acceptContactRequest(
            @PathVariable Long contactId, 
            @PathVariable int adminId) {
        
        String result = contactFormService.acceptContactRequest(contactId, adminId);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/reject/{id}")
    public ResponseEntity<String> rejectContact(@PathVariable Long id) {
        boolean isRejected = contactFormService.rejectContact(id);

        if (isRejected) {
            return ResponseEntity.ok("Contact request rejected successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contact request not found");
        }
    }
}
