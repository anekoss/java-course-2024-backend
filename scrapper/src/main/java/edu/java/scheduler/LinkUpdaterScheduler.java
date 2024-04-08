package edu.java.scheduler;

import edu.java.client.BotClient;
import edu.java.client.dto.LinkUpdateRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class LinkUpdaterScheduler {
    private final LinkUpdaterService linkUpdaterService;
    private final BotClient botClient;
    private final long limit;

    @Scheduled(fixedDelayString = "#{@scheduler.forceCheckDelay}")
    public void update() {
        List<LinkUpdateRequest> updatedLink = linkUpdaterService.getUpdates(limit);
        updatedLink.forEach(botClient::linkUpdates);
        log.info("update {} links", updatedLink.size());
    }
}
