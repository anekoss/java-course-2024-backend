package edu.java.service.jdbc;

import edu.java.client.BotClient;
import edu.java.client.dto.LinkUpdateRequest;
import edu.java.domain.Link;
import edu.java.repository.ChatRepository;
import edu.java.repository.LinkRepository;
import edu.java.service.GithubUpdater;
import edu.java.service.LinkUpdaterService;
import edu.java.service.StackOverflowUpdater;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JdbcLinkUpdaterService implements LinkUpdaterService {
    private final LinkRepository linkRepository;
    private final GithubUpdater githubUpdater;
    private final StackOverflowUpdater stackOverflowUpdater;
    private final BotClient botClient;
    private final ChatRepository chatRepository;
    private final Long limit = 10L;

    public int update() {
        List<Link> links = linkRepository.findStaleLinks(limit);
        List<Link> updatedLinks = new ArrayList<>();
        for (Link link : links) {
            Link updatedLink = link;
            switch (link.getType()) {
                case STACKOVERFLOW -> {
                    updatedLink = stackOverflowUpdater.update(link);
                    break;
                }
                case GITHUB -> {
                    updatedLink = githubUpdater.update(link);
                    break;
                }
            }
            linkRepository.update(link.getId(), updatedLink.getUpdatedAt(), OffsetDateTime.now());
            if (updatedLink.getUpdatedAt().isAfter(link.getUpdatedAt())) {
                updatedLinks.add(updatedLink);
            }
        }
        return sendUpdates(updatedLinks);
    }

    private int sendUpdates(List<Link> links) {
        for (Link link : links) {
            Long[] chatIds = chatRepository.findChatIdByLinkId(link.getId()).toArray(Long[]::new);
            botClient.linkUpdates(new LinkUpdateRequest(1L, link.getUri().toString(), "updated", chatIds));
        }
        return links.size();
    }
}
