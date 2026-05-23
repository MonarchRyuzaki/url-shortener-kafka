package com.shiryu.url_shortener_kafka.services;

import com.shiryu.url_shortener_kafka.entities.UrlMapping;
import com.shiryu.url_shortener_kafka.exceptions.InvalidUrlException;
import com.shiryu.url_shortener_kafka.exceptions.UrlNotFoundException;
import com.shiryu.url_shortener_kafka.repositories.UrlMappingRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private final UrlMappingRepository repository;
    private final Validator validator;
//    private final KafkaTemplate<String, String> kafkaTemplate;

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
        return savedMapping;
    }

    private void validateUrl(String url) {
        UrlContainer container = new UrlContainer(url);
        Set<ConstraintViolation<UrlContainer>> violations = validator.validate(container);
        if (!violations.isEmpty()) {
            throw new InvalidUrlException("Invalid URL: " + violations.iterator().next().getMessage());
        }
    }

    private record UrlContainer(@URL(message = "Invalid URL format") String url) {}

    /**
     * Retrieves the original URL and handles click stream logic.
     * Logic should include cache checks, DB lookup, expiration check,
     * and producing a click event to Kafka.
     */
    public String resolveUrl(String shortUrlKey) {
        // TODO: (Optional) Check cache (e.g., Redis) for the shortUrlKey
        validateShortUrl(shortUrlKey);
        Optional<UrlMapping> mapping = repository.findByShortUrlKey(shortUrlKey);
        if (mapping.isEmpty()) {
            throw new UrlNotFoundException("No Url found with shortUrlKey: " + shortUrlKey);
        }
        if (mapping.get().getExpiresAt() .isBefore(LocalDateTime.now())) {
            throw new UrlNotFoundException("No Url found with shortUrlKey: " + shortUrlKey);
        }
        // TODO: Produce a click event to Kafka for asynchronous click count updates
        return mapping.get().getOriginalUrl();
    }

    private void validateShortUrl(String shortUrlKey) {
        if (shortUrlKey.length() != 7) {
            throw new InvalidUrlException("Invalid shortUrlKey: " + shortUrlKey);
        }
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
