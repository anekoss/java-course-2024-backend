package edu.java.scrapper.queue;

import edu.java.client.dto.LinkUpdateRequest;
import edu.java.queue.ScrapperQueueProducer;
import edu.java.scrapper.KafkaIntegrationTest;
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
import org.springframework.kafka.support.serializer.JsonDeserializer;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "app.use-queue=true")
public class ScrapperQueueProducerTest extends KafkaIntegrationTest {

    @Autowired
    private ScrapperQueueProducer queueProducer;

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
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }

    @Test
    void testSend_consumerShouldReceiveUpdateRequest() {
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(1L, "url", "updated", new long[] {1L, 2L});
        queueProducer.send(linkUpdateRequest);
        try (KafkaConsumer<String, LinkUpdateRequest> kafkaConsumer = new KafkaConsumer<>(getConsumerProps())) {
            kafkaConsumer.subscribe(List.of(topic));
            ConsumerRecords<String, LinkUpdateRequest> records = kafkaConsumer.poll(Duration.ofSeconds(10));
            assert !records.isEmpty();
            assertEquals(records.iterator().next().value().id(), linkUpdateRequest.id());
        }
    }

}
