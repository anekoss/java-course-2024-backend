package edu.java.service.jdbc;

import edu.java.domain.Link;
import edu.java.repository.ChatRepository;
import edu.java.repository.LinkRepository;
import edu.java.service.LinkService;
import edu.java.service.LinkTypeService;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;
    private final LinkTypeService linkTypeService;

    @Override
    public Link add(long tgChatId, URI url) {
        List<Link> links = linkRepository.findByChatId(tgChatId);
        if (links.stream().anyMatch(link -> link.getUri() == url)) {
            throw new AlreadyExistException();
        }
        Link link = new Link(url, linkTypeService.getType(url.getHost()));
        linkRepository.save(tgChatId, link);
        return link;
    }

    @Override
    public Link remove(long tgChatId, URI url) {
        List<Link> links = linkRepository.findByChatId(tgChatId);
        if (links.stream().anyMatch(link -> link.getUri() == url)) {
            throw new AlreadyExistException();
        }
        linkRepository.delete(tgChatId, url);
        return
    }

    @Override
    public List<Link> listAll(long tgChatId) {
        List<Link> links = linkRepository.findByChatId(tgChatId);
        if (links == null) {
            return List.of();
        }
        return links;
    }
}
