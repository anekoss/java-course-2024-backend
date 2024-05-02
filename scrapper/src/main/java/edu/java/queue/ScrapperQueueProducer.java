package edu.java.queue;

import edu.java.client.dto.LinkUpdateRequest;
import edu.java.client.exception.CustomWebClientException;
import edu.java.configuration.ApplicationConfig;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapperQueueProducer {
    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;
    private final ApplicationConfig.KafkaConfig kafkaConfig;

    public void send(LinkUpdateRequest update) {
        try {
            kafkaTemplate.send(kafkaConfig.topic().name(), update).get(kafkaConfig.timeout(), TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            log.warn(e.getMessage());
            throw new CustomWebClientException();
        }
    }
}
