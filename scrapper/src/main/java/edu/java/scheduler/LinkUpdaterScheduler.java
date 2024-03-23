package edu.java.scheduler;

import edu.java.domain.Link;
import edu.java.domain.UpdateType;
import edu.java.service.LinkUpdaterService;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class LinkUpdaterScheduler {
    private final LinkUpdaterService linkUpdaterService;

    @Scheduled(fixedDelayString = "#{@scheduler.forceCheckDelay}")
    public void update() {
        Map<Link, UpdateType> updatedLink = linkUpdaterService.update();
        long cntUpdate = linkUpdaterService.sendUpdates(updatedLink);
        log.info("update {} links", cntUpdate);
    }
}
