package edu.java.bot.service;

import com.pengrad.telegrambot.response.SendResponse;
import edu.java.bot.KafkaIntegrationTest;
import edu.java.bot.bot.TelegramBotImpl;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class KafkaUpdateServiceTest extends KafkaIntegrationTest {

    private final static String DLQ = "dlq";
    @MockBean
    private UpdatesSenderService updatesSenderService;
    @MockBean
    private TelegramBotImpl bot;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Value("${app.kafka-config.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${app.kafka-config.topic.name}")
    private String topic;

    private Map<String, Object> getConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("properties.spring.json.trusted.packages", "*");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
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
        SendResponse response = Mockito.mock(SendResponse.class);
        when(bot.execute(any())).thenReturn(response);
        Mockito.when(response.isOk()).thenReturn(false);
        Mockito.when(response.description()).thenReturn("Server exception");
        Mockito.when(response.errorCode()).thenReturn(500);
        String linkUpdateRequest =
            "{ \"id\": 1, \"url\": \"url\", \"description\": \"updated\", \"tgChatIds\": [1, 2] }";
        kafkaTemplate.send(topic, linkUpdateRequest);
        try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(getConsumerProps())) {
            kafkaConsumer.subscribe(List.of(DLQ));
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(15));
            for (ConsumerRecord<String, String> record : records) {
                Assertions.assertEquals("Server exception", record.value());
            }

        }
    }
}
