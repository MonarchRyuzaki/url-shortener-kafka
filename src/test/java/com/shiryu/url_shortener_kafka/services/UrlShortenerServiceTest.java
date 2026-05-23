package com.shiryu.url_shortener_kafka.services;

import com.shiryu.url_shortener_kafka.entities.UrlMapping;
import com.shiryu.url_shortener_kafka.exceptions.InvalidUrlException;
import com.shiryu.url_shortener_kafka.repositories.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    @Mock
    private UrlMappingRepository repository;

    @InjectMocks
    private UrlShortenerService urlShortenerService;

    private final String validUrl = "https://www.google.com";

    @Test
    void shortenUrl_ValidUrl_Success() {
        when(repository.findByOriginalUrl(validUrl)).thenReturn(Optional.empty());
        when(repository.save(any(UrlMapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UrlMapping result = urlShortenerService.shortenUrl(validUrl);

        assertNotNull(result);
        assertEquals(validUrl, result.getOriginalUrl());
        assertNotNull(result.getShortUrlKey());
        assertEquals(7, result.getShortUrlKey().length());
        verify(repository, times(1)).save(any(UrlMapping.class));
    }

    @Test
    void shortenUrl_ExistingUrl_ReturnsExistingMapping() {
        UrlMapping existingMapping = UrlMapping.builder()
                .originalUrl(validUrl)
                .shortUrlKey("abcd123")
                .build();
        when(repository.findByOriginalUrl(validUrl)).thenReturn(Optional.of(existingMapping));

        UrlMapping result = urlShortenerService.shortenUrl(validUrl);

        assertEquals(existingMapping, result);
        verify(repository, never()).save(any(UrlMapping.class));
    }

    @Test
    void shortenUrl_InvalidUrl_ThrowsException() {
        String invalidUrl = "not-a-url";

        assertThrows(InvalidUrlException.class, () -> urlShortenerService.shortenUrl(invalidUrl));
    }

    @Test
    void shortenUrl_EmptyUrl_ThrowsException() {
        assertThrows(InvalidUrlException.class, () -> urlShortenerService.shortenUrl(""));
        assertThrows(InvalidUrlException.class, () -> urlShortenerService.shortenUrl(null));
    }

    @Test
    void shortenUrl_ShortUrlKeyCollision_GeneratesNewKey() {
        when(repository.findByOriginalUrl(validUrl)).thenReturn(Optional.empty());
        // Mock collision once
        when(repository.findByShortUrlKey(anyString()))
                .thenReturn(Optional.of(new UrlMapping()))
                .thenReturn(Optional.empty());
        
        when(repository.save(any(UrlMapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UrlMapping result = urlShortenerService.shortenUrl(validUrl);

        assertNotNull(result);
        verify(repository, times(2)).findByShortUrlKey(anyString());
        verify(repository, times(1)).save(any(UrlMapping.class));
    }
}
