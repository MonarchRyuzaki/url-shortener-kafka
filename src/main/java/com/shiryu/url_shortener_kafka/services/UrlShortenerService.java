package com.shiryu.url_shortener_kafka.services;

import com.shiryu.url_shortener_kafka.entities.UrlMapping;
import com.shiryu.url_shortener_kafka.exceptions.InvalidUrlException;
import com.shiryu.url_shortener_kafka.repositories.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private final UrlMappingRepository repository;
//    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    /**
     * Shortens the given original URL.
     * Logic should include validation, checking for existing mappings,
     * generating a unique short key, saving to DB, and producing a Kafka event.
     */
    public UrlMapping shortenUrl(String originalUrl) {
        validateUrl(originalUrl);

        Optional<UrlMapping> existingMapping = repository.findByOriginalUrl(originalUrl);
        if (existingMapping.isPresent()) {
            return existingMapping.get();
        }

        String shortUrlKey;
        do {
            shortUrlKey = UUID.randomUUID().toString().substring(0, 7);
        } while (repository.findByShortUrlKey(shortUrlKey).isPresent());

        UrlMapping mapping = UrlMapping.builder()
                .originalUrl(originalUrl)
                .shortUrlKey(shortUrlKey)
                .build();
        UrlMapping savedMapping = repository.save(mapping);
        // TODO: Produce a Kafka event to a topic (e.g., 'url-shortened')
        return savedMapping;
    }

    private void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new InvalidUrlException("URL cannot be empty");
        }
        if (!URL_PATTERN.matcher(url).matches()) {
            throw new InvalidUrlException("Invalid URL format: " + url);
        }
    }

    /**
     * Retrieves the original URL and handles click stream logic.
     * Logic should include cache checks, DB lookup, expiration check,
     * and producing a click event to Kafka.
     */
    public String resolveUrl(String shortUrlKey) {
        // TODO: (Optional) Check cache (e.g., Redis) for the shortUrlKey
        // TODO: Lookup the UrlMapping in the repository by shortUrlKey
        // TODO: Check if the URL has expired
        // TODO: Produce a click event to Kafka for asynchronous click count updates
        // TODO: Return the original URL
        throw new UnsupportedOperationException("TODO: Implement resolveUrl logic");
    }

    /**
     * Fetches statistics for a given short URL key.
     */
    public Map<String, Object> getUrlStats(String shortUrlKey) {
        // TODO: Fetch UrlMapping from DB
        // TODO: (Optional) Fetch additional metrics from cache/Kafka
        // TODO: Return a map containing originalUrl, clickCount, createdAt, expiresAt, and status
        throw new UnsupportedOperationException("TODO: Implement getUrlStats logic");
    }
}
