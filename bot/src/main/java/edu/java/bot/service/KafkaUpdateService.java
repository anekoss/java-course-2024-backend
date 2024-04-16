package edu.java.bot.service;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.controller.dto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaUpdateService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UpdatesSenderService updatesSenderService;
    private final ApplicationConfig.KafkaConfig kafkaConfig;

    @KafkaListener(id = "id",
                   topics = "${app.kafka-config.topic.name}",
                   containerFactory = "kafkaListenerContainerFactory")
    public void send(LinkUpdateRequest request) {
        try {
            updatesSenderService.sendUpdates(request);
        } catch (Exception e) {
            log.warn("send updates exception {}", e.getMessage());
            kafkaTemplate.send(kafkaConfig.topicDlq().name(), e.getMessage());
        }
    }
}
