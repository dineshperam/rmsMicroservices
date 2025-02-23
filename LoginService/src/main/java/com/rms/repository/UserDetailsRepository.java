package com.rms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.rms.model.UserDetails;

import jakarta.transaction.Transactional;


@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Integer>{
	long countByUsernameAndPassword(String username,String password);
	
	UserDetails findByUsername(String userName);
	List<UserDetails> findByRole(String role);
	
	Optional<UserDetails> findByEmail(String email);
	
	@Modifying
    @Transactional
    @Query("UPDATE UserDetails u SET u.isActive = false WHERE u.id = :userId")
    void softDeleteUser(@Param("userId") Long userId);
	
	List<UserDetails> findByManagerId(int managerId);
	
}