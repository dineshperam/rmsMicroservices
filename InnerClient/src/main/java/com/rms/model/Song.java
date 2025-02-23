package com.rms.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class Song {
	

	private int songId;
	

	private int artistId;
	

	private String title;
	

	@JsonFormat(pattern = "yyyy-MM-dd") // Ensures correct JSON serialization/deserialization
	private LocalDate releaseDate;
	

	private String collaborators;
	

	private String genre;
	
}
