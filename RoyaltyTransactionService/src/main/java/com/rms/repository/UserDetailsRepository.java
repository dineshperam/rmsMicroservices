package com.rms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rms.model.UserDetails;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Integer> {

}
