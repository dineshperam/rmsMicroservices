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

public class Streams {

    private int streamId;

    private int songId;

    private Date streamDate;

    private long streamCount;

    private int userId;

    private String status;
    
	public Streams(int streamId, int songId, Date streamDate, int userId) {
		super();
		this.streamId = streamId;
		this.songId = songId;
		this.streamDate = streamDate;
		this.userId = userId;
	}
}