package com.rms.repository;
 

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;


import com.rms.model.Streams;



public interface StreamsRepository extends JpaRepository<Streams, Integer> {
 
	
	List<Streams> findBySongId(int songId);

	
	List<Streams> findByStatus(String status);
	

}

	