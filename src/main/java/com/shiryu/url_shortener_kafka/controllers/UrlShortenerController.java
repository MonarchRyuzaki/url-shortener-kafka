package com.shiryu.url_shortener_kafka.controllers;

import com.shiryu.url_shortener_kafka.entities.UrlMapping;
import com.shiryu.url_shortener_kafka.services.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UrlShortenerController {

    private final UrlShortenerService service;

    /**
     * Endpoint to shorten a URL.
     * Expects a JSON body: { "url": "http://example.com" }
     */
    @PostMapping("/shorten")
    public ResponseEntity<UrlMapping> shortenUrl(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        UrlMapping mapping = service.shortenUrl(url);
        return ResponseEntity.status(201).body(mapping);
    }

    /**
     * Endpoint for redirection.
     * GET /api/v1/{shortUrlKey}
     */
    @GetMapping("/api/v1/{shortUrlKey}")
    public ResponseEntity<Void> redirectToOriginal(@PathVariable String shortUrlKey) {
        // TODO: Call service.resolveUrl
        // TODO: Return 301 or 302 redirect to the original URL
        throw new UnsupportedOperationException("TODO: Implement redirectToOriginal endpoint");
    }

    /**
     * Endpoint to fetch URL statistics.
     * GET /api/v1/stats/{shortUrlKey}
     */
    @GetMapping("/stats/{shortUrlKey}")
    public ResponseEntity<Map<String, Object>> getUrlStats(@PathVariable String shortUrlKey) {
        // TODO: Call service.getUrlStats
        // TODO: Return 200 OK with the stats map
        throw new UnsupportedOperationException("TODO: Implement getUrlStats endpoint");
    }
}
