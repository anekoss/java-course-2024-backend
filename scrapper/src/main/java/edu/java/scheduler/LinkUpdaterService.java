package edu.java.scheduler;

import edu.java.client.dto.LinkUpdateRequest;
import edu.java.client.exception.CustomWebClientException;
import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.service.LinkService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class LinkUpdaterService {
    private final LinkService linkService;
    private final Map<LinkType, UpdateChecker> updateCheckerMap;

    public List<LinkUpdateRequest> getUpdates(long limit) {
        List<Link> links = linkService.getStaleLinks(limit);
        List<LinkUpdateRequest> updates = new ArrayList<>();
        links.forEach(link -> {
            try {
                Link updatedLink = updateCheckerMap.get(link.getLinkType()).check(link);
                if (updatedLink.getCheckedAt().isAfter(link.getCheckedAt())) {
                    linkService.update(link.getId(), updatedLink.getUpdatedAt(), updatedLink.getCheckedAt());
                    if (updatedLink.getUpdatedAt().isAfter(link.getUpdatedAt())) {
                        long[] chatIds = linkService.getChatIdsByLinkId(updatedLink.getId());
                        updates.add(new LinkUpdateRequest(link.getId(), link.getUri().toString(), "updates", chatIds));
                    }
                }
            } catch (CustomWebClientException e) {
                log.warn(e.getMessage());
            }
        });
        return updates;
    }

}
