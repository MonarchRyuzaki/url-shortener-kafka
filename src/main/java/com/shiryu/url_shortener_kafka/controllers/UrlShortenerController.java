package com.shiryu.url_shortener_kafka.controllers;

import com.shiryu.url_shortener_kafka.entities.UrlMapping;
import com.shiryu.url_shortener_kafka.services.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URL;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UrlShortenerController {

    private final UrlShortenerService service;

    /**
     * Endpoint to shorten a URL.
     * Expects a JSON body: { "url": "http://example.com" }
     */
    @PostMapping("/api/v1/shorten")
    public ResponseEntity<UrlMapping> shortenUrl(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        UrlMapping mapping = service.shortenUrl(url);
        return ResponseEntity.status(201).body(mapping);
    }

    /**
     * Endpoint for redirection.
     * GET /{shortUrlKey}
     */
    @GetMapping("/{shortUrlKey}")
    public ResponseEntity<Void> redirectToOriginal(@PathVariable String shortUrlKey) {
        String originalUrl = service.resolveUrl(shortUrlKey);
        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).location(URI.create(originalUrl)).build();
    }

    /**
     * Endpoint to fetch URL statistics.
     * GET /api/v1/stats/{shortUrlKey}
     */
    @GetMapping("/api/v1/stats/{shortUrlKey}")
    public ResponseEntity<Map<String, Object>> getUrlStats(@PathVariable String shortUrlKey) {
        // TODO: Call service.getUrlStats
        // TODO: Return 200 OK with the stats map
        throw new UnsupportedOperationException("TODO: Implement getUrlStats endpoint");
    }
}
