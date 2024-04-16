package edu.java.scheduler;

import edu.java.client.dto.LinkUpdateRequest;
import edu.java.service.LinkUpdaterService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableScheduling
public class LinkUpdaterScheduler {
    private final LinkUpdaterService linkUpdaterService;
    private final UpdateSender updateSender;
    private final Long limit;

    public LinkUpdaterScheduler(
        LinkUpdaterService linkUpdaterService,
        UpdateSender updateSender,
        @Value("#{@scheduler.limit()}") Long limit
    ) {
        this.linkUpdaterService = linkUpdaterService;
        this.updateSender = updateSender;
        this.limit = limit;
    }

    @Scheduled(fixedDelayString = "#{@scheduler.forceCheckDelay}")
    public void update() {
        List<LinkUpdateRequest> updatedLinks = linkUpdaterService.getUpdates(limit);
        updatedLinks.forEach(updateSender::send);
        log.info("update {} links", updatedLinks.size());
    }
}
