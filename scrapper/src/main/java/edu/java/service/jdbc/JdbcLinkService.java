package edu.java.service.jdbc;

import edu.java.domain.Chat;
import edu.java.domain.Link;
import edu.java.exception.AlreadyExistException;
import edu.java.exception.ChatNotFoundException;
import edu.java.repository.ChatRepository;
import edu.java.repository.LinkRepository;
import edu.java.service.LinkService;
import edu.java.service.LinkTypeService;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;
    private final LinkTypeService linkTypeService;

    @Override
    public Link add(long tgChatId, URI url) throws ChatNotFoundException, AlreadyExistException {
        Optional<Chat> optionalChat = chatRepository.findByChatId(tgChatId);
        if (optionalChat.isEmpty()) {
            throw new ChatNotFoundException();
        }
        List<Link> links = linkRepository.findByChatId(optionalChat.get().getId());
        if (links.stream().anyMatch(link -> link.getUri() == url)) {
            throw new AlreadyExistException();
        }
        Link link = new Link(url, linkTypeService.getType(url.getHost()));
        linkRepository.save(tgChatId, link);
        return link;
    }

    @Override
    public Link remove(long tgChatId, URI url) throws ChatNotFoundException, ResourceNotFoundException {
        Optional<Chat> optionalChat = chatRepository.findByChatId(tgChatId);
        if (optionalChat.isEmpty()) {
            throw new ChatNotFoundException();
        }
        List<Link> links = linkRepository.findByChatId(optionalChat.get().getId());
        Optional<Link> link = links.stream().filter(link1 -> link1.getUri() == url).findFirst();
        if (link.isEmpty()) {
            throw new ResourceNotFoundException("Ссылка не найдена");
        }
        linkRepository.delete(tgChatId, url);
        return link.get();
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
