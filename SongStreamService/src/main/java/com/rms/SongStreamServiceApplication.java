package com.rms;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;

import com.rms.service.StreamsService;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@EnableDiscoveryClient
@RequiredArgsConstructor
public class SongStreamServiceApplication {
	
	private final StreamsService streamService;
	 private static final Logger logger = Logger.getLogger(SongStreamServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SongStreamServiceApplication.class, args);
	}
	
	// Run every hour to insert new stream records
    @Scheduled(fixedRate = 3600000)
    public void scheduleStreamInsertion() {
        logger.info("Inserting new stream records: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        streamService.insertNewStreams();
    }

}
