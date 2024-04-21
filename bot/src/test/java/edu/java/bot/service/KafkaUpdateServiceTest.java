package edu.java.bot.service;

import edu.java.bot.KafkaIntegrationTest;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.codec.DecodingException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class KafkaUpdateServiceTest extends KafkaIntegrationTest {

    @MockBean
    private UpdatesSenderService updatesSenderService;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private KafkaUpdateService kafkaUpdateService;
    @Value("${app.kafka-config.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${app.kafka-config.topic.name}")
    private String topic;
    private String dlq = "dlq";

    private Map<String, Object> getConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("properties.spring.json.trusted.packages", "*");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }

    @Test
    void testUpdateService_shouldCorrectlyExecuteBot() {
        doNothing().when(updatesSenderService).sendUpdates(any());
        String linkUpdateRequest =
            "{ \"id\": 1, \"url\": \"url\", \"description\": \"updated\", \"tgChatIds\": [1, 2] }";
        kafkaTemplate.send(topic, linkUpdateRequest);
        await()
            .pollInterval(Duration.ofSeconds(3))
            .atMost(15, SECONDS)
            .untilAsserted(() -> verify(updatesSenderService, times(1)).sendUpdates(any()));
    }

    @Test
    void testUpdateService_shouldSendExceptionToDlqTopic() {
        doThrow(DecodingException.class).when(updatesSenderService).sendUpdates(any());
        String linkUpdateRequest =
            "{ \"id\": 1, \"url\": \"url\", \"description\": \"updated\", \"tgChatIds\": [1, 2] }";
        kafkaTemplate.send(topic, linkUpdateRequest);
        ConsumerRecords<String, String> records;
        try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(getConsumerProps())) {
            kafkaConsumer.subscribe(List.of(dlq));
            records = kafkaConsumer.poll(Duration.ofSeconds(20));
        }
        assert !records.isEmpty();
        assert !records.iterator().next().value().isEmpty();
    }

}
