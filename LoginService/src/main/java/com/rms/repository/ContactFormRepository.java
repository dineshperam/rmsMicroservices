package com.rms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rms.model.ContactForm;

@Repository
public interface ContactFormRepository extends JpaRepository<ContactForm, Long> {
	Optional<ContactForm> findByEmail(String email);

}