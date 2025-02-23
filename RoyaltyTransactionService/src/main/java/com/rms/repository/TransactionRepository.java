package com.rms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rms.model.Transactions;

public interface TransactionRepository extends JpaRepository<Transactions, Integer> {
	
List<Transactions> findByReceiver(int id);
	
	List<Transactions> findByManagerId(int id);
	
	List<Transactions> findByReceiverIn(List<Integer> receiverIds);

}
