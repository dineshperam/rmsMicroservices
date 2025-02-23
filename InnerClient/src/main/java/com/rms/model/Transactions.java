package com.rms.model;

import java.util.Date;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Transactions {
	

	private int transactionId;
	

	private int receiver;
	

	private int sender;
	
	private int royaltyId;
	

	private Date transactionDate;

	private double transactionAmount;
	

	private int managerId;

	private String transactionType;
	
	public Transactions(int receiver, int sender, int royaltyId, Date transactionDate,
		            double transactionAmount, Integer managerId, String transactionType) {
		this.receiver = receiver;
		this.sender = sender;
		this.royaltyId = royaltyId;
		this.transactionDate = transactionDate;
		this.transactionAmount = transactionAmount;
		this.managerId = managerId;
		this.transactionType = transactionType;
}
}
