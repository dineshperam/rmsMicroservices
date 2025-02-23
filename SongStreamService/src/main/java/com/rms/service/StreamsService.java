package com.rms.service;

import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.rms.CONSTANTS;
import com.rms.exeptions.InvalidStreamException;
import com.rms.exeptions.NotFoundException;
import com.rms.model.Song;
import com.rms.model.Streams;
import com.rms.repository.SongRepository;
import com.rms.repository.StreamsRepository;

@Service
public class StreamsService {
	
	

    private final StreamsRepository streamRepository;
    private final SongRepository songRepository;
    private static final Logger logger = Logger.getLogger(StreamsService.class);

    public StreamsService(StreamsRepository streamRepository, SongRepository songRepository) {
        this.streamRepository = streamRepository;
        this.songRepository = songRepository;
    }

    public List<Streams> showStream() {
        logger.info("Fetching all streams.");
        return streamRepository.findAll();
    }

    public Streams searchStreamById(int streamId) {
        logger.info("Searching for stream with ID: " + streamId);
        return streamRepository.findById(streamId)
                .orElseThrow(() -> {
                    logger.warn("Stream with ID " + streamId + CONSTANTS.NOT_FOUND_MSG);
                    return new NotFoundException("Stream with ID " + streamId + CONSTANTS.NOT_FOUND_MSG);
                });
    }

    public void addStream(Streams stream) {
        logger.info("Adding new stream for song ID: " + stream.getSongId());
        if (stream.getSongId() == 0) {
            logger.warn("Attempt to add a stream with an invalid song ID.");
            throw new InvalidStreamException("Stream must be associated with a valid song ID");
        }
        streamRepository.save(stream);
    }

    public void updateStream(Streams updatedStream) {
        logger.info("Updating stream with ID: " + updatedStream.getStreamId());
        if (!streamRepository.existsById(updatedStream.getStreamId())) {
            logger.warn("Cannot update, stream with ID " + updatedStream.getStreamId() + CONSTANTS.NOT_FOUND_MSG);
            throw new NotFoundException("Cannot update, stream with ID " + updatedStream.getStreamId() + CONSTANTS.NOT_FOUND_MSG);
        }
        streamRepository.save(updatedStream);
    }

    public void deleteStream(int id) {
        logger.info("Deleting stream with ID: " + id);
        if (!streamRepository.existsById(id)) {
            logger.warn("Cannot delete, stream with ID " + id + CONSTANTS.NOT_FOUND_MSG);
            throw new NotFoundException("Cannot delete, stream with ID " + id + CONSTANTS.NOT_FOUND_MSG);
        }
        streamRepository.deleteById(id);
    }

    public List<Streams> searchBysongId(int songId) {
        logger.info("Fetching streams for song with ID: " + songId);
        List<Streams> streams = streamRepository.findBySongId(songId);
        if (streams.isEmpty()) {
            logger.warn("No streams found for song with ID " + songId);
            throw new NotFoundException("No streams found for song with ID " + songId);
        }
        return streams;
    }

    public List<Streams> getInProgressStreams() {
        logger.info("Fetching all streams with status 'IN PROGRESS'.");
        return streamRepository.findByStatus("IN PROGRESS");
    }

    private final Random random = new Random();

    public void insertNewStreams() {
        logger.info("Inserting new streams for all songs.");
        List<Song> songs = songRepository.findAll();
        for (Song song : songs) {
            int streamCount = 500 + random.nextInt(201);
            logger.info("Generating " + streamCount + " new streams for song ID: " + song.getSongId());
            Streams newStream = new Streams(song.getSongId(), streamCount, new Date(), song.getArtistId());
            streamRepository.save(newStream);
        }
    }
}
