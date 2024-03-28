package edu.java.service.jdbc;

import edu.java.client.BotClient;
import edu.java.client.dto.LinkUpdateRequest;
import edu.java.client.exception.BadResponseBodyException;
import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.domain.TgChat;
import edu.java.domain.UpdateType;
import edu.java.repository.LinkRepository;
import edu.java.service.LinkUpdaterService;
import edu.java.service.UpdateChecker;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class JdbcLinkUpdaterService implements LinkUpdaterService {
    private final LinkRepository linkRepository;
    private final Map<LinkType, UpdateChecker> updateCheckerMap;
    private final BotClient botClient;
    private final Long limit;

    public Map<Link, UpdateType> update() {
        List<Link> links = linkRepository.findStaleLinks(limit);
        Map<Link, UpdateType> updates = new HashMap<>();
        for (Link link : links) {
            Map.Entry<Link, UpdateType> updatedLink = updateCheckerMap.get(link.getLinkType()).check(link);
            if (updatedLink.getValue() != UpdateType.NO_UPDATE) {
                linkRepository.update(
                    link.getId(),
                    updatedLink.getKey().getUpdatedAt(),
                    updatedLink.getKey().getCheckedAt()
                );
                updates.put(updatedLink.getKey(), updatedLink.getValue());
            }
        }
        return updates;
    }

    public long sendUpdates(Map<Link, UpdateType> links) {
        long countUpdate = 0;
        for (Link link : links.keySet()) {
            try {
                Long[] chatIds = link.getTgChats().stream().map(TgChat::getChatId).toArray(Long[]::new);
                if (chatIds != null && chatIds.length != 0) {
                    botClient.linkUpdates(new LinkUpdateRequest(
                        1L,
                        link.getUri().toString(),
                        links.get(link).getMessage(),
                        chatIds
                    ));
                    countUpdate++;
                }
            } catch (BadResponseBodyException e) {
                log.error(e.getMessage());
            }
        }
        return countUpdate;
    }
}
