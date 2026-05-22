package com.shiryu.url_shortener_kafka.repositories;

import com.shiryu.url_shortener_kafka.entities.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    Optional<UrlMapping> findByShortUrlKey(String shortUrlKey);
    Optional<UrlMapping> findByOriginalUrl(String originalUrl);
}
