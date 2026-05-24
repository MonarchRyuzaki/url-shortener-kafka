package com.shiryu.url_shortener_kafka.consumer;

import com.shiryu.url_shortener_kafka.entities.UrlMapping;
import com.shiryu.url_shortener_kafka.repositories.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClickEventConsumer {

    private final UrlMappingRepository  urlMappingRepository;

    @Transactional
    @KafkaListener(topics = "url-clicks", groupId = "url-shortener-click-consumer-group")
    public void consumeClickBatch(List<String> shortUrlKeys) {
        if (shortUrlKeys.isEmpty()) {return;}
        Map<String, Long> clickCounts = shortUrlKeys.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<UrlMapping> mappings = urlMappingRepository.findAllByShortUrlKeyIn(clickCounts.keySet());

        for (UrlMapping mapping : mappings) {
            long newClicks = clickCounts.get(mapping.getShortUrlKey());
            mapping.setClickCount(mapping.getClickCount() + (int) newClicks);
        }

        urlMappingRepository.saveAll(mappings);
    }
}
