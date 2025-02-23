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

 
public class Royalty {

	private int royaltyId;

	private int songId;

	private Date calculatedDate;
	private long totalStreams;

	private double royaltyAmount;

	private int artistId;

	private String status;
}