package edu.java.service;

import edu.java.client.dto.LinkUpdateRequest;
import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.scheduler.UpdateChecker;
import edu.java.scheduler.dto.LinkUpdate;
import edu.java.scheduler.dto.UpdateType;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LinkUpdaterService {
    private final LinkService linkService;
    private final Map<LinkType, UpdateChecker> updateCheckerMap;

    @Transactional
    public List<LinkUpdateRequest> getUpdates(long limit) {
        List<Link> links = linkService.getStaleLinks(limit);
        List<LinkUpdateRequest> updates = new ArrayList<>();
        links.forEach(link -> {
            LinkUpdate linkUpdateType = updateCheckerMap.get(link.getLinkType()).check(link);
            Link updatedLink = linkUpdateType.link();
            linkService.update(link.getId(), updatedLink.getUpdatedAt(), updatedLink.getCheckedAt());
            if (linkUpdateType.type() != UpdateType.NO_UPDATE) {
                long[] chatIds = linkService.getChatIdsByLinkId(updatedLink.getId());
                if (chatIds.length != 0) {
                    updates.add(new LinkUpdateRequest(
                        link.getId(),
                        link.getUri().toString(),
                        linkUpdateType.type().getMessage(),
                        chatIds
                    ));
                }
            }
        });
        return updates;
    }
}
