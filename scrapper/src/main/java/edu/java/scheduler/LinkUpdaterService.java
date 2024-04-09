package edu.java.scheduler;

import edu.java.client.dto.LinkUpdateRequest;
import edu.java.client.exception.CustomWebClientException;
import edu.java.domain.LinkEntity;
import edu.java.domain.LinkType;
import edu.java.service.LinkService;
import jakarta.transaction.Transactional;
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

    @Transactional
    public List<LinkUpdateRequest> getUpdates(long limit) {
        List<LinkEntity> linkEntities = linkService.getStaleLinks(limit);
        List<LinkUpdateRequest> updates = new ArrayList<>();
        linkEntities.forEach(link -> {
            try {
                LinkEntity updatedLinkEntity = updateCheckerMap.get(link.getLinkType()).check(link);
                if (updatedLinkEntity.getCheckedAt().isAfter(link.getCheckedAt())) {
                    linkService.update(link.getId(), updatedLinkEntity.getUpdatedAt(), updatedLinkEntity.getCheckedAt());
                    if (updatedLinkEntity.getUpdatedAt().isAfter(link.getUpdatedAt())) {
                        long[] chatIds = linkService.getChatIdsByLinkId(updatedLinkEntity.getId());
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
