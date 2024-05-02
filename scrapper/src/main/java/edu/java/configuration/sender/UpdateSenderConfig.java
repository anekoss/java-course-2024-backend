package edu.java.configuration.sender;

import edu.java.client.BotClient;
import edu.java.configuration.ApplicationConfig;
import edu.java.queue.ScrapperQueueProducer;
import edu.java.scheduler.UpdateSender;
import edu.java.scheduler.sender.HttpUpdateSender;
import edu.java.scheduler.sender.QueueUpdateSender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UpdateSenderConfig {
    private final ApplicationConfig applicationConfig;
    private final ScrapperQueueProducer scrapperQueueProducer;
    private final BotClient botClient;

    @Bean
    public UpdateSender updateSender() {
        return applicationConfig.useQueue() ? new QueueUpdateSender(scrapperQueueProducer)
            : new HttpUpdateSender(botClient);
    }
}
