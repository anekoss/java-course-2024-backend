package edu.java.service.jdbc;

import edu.java.controller.dto.LinkResponse;
import edu.java.controller.dto.ListLinksResponse;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.controller.exception.LinkAlreadyExistException;
import edu.java.controller.exception.LinkNotFoundException;
import edu.java.domain.ChatLink;
import edu.java.domain.Link;
import edu.java.repository.ChatLinkRepository;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import edu.java.service.LinkService;
import edu.java.service.util.LinkTypeService;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final TgChatRepository tgChatRepository;
    private final LinkRepository linkRepository;
    private final LinkTypeService linkTypeService;
    private final ChatLinkRepository chatLinkRepository;

    @Override
    @Transactional
    public LinkResponse add(long chatId, URI url) throws ChatNotFoundException, LinkAlreadyExistException {
        Link link = new Link().setUri(url)
            .setLinkType(linkTypeService.getType(url.getHost()))
            .setCheckedAt(OffsetDateTime.now())
            .setUpdatedAt(OffsetDateTime.now());
        long linkId = linkRepository.add(link);
        long tgChatId = tgChatRepository.findByChatId(chatId).getId();
        chatLinkRepository.add(new ChatLink(tgChatId, linkId));
        return new LinkResponse(linkId, url);
    }

    @Override
    @Transactional
    public LinkResponse remove(long chatId, URI url)
        throws ChatNotFoundException, LinkNotFoundException {
        long tgChatId = tgChatRepository.findByChatId(chatId).getId();
        Optional<Link> link = linkRepository.findByUri(url);
        if (link.isEmpty()) {
            throw new LinkNotFoundException();
        }
        Long linkId = link.get().getId();
        chatLinkRepository.remove(new ChatLink(tgChatId, linkId));
        List<ChatLink> chatLinks = chatLinkRepository.findByLinkId(linkId);
        if (chatLinks.isEmpty()) {
            linkRepository.remove(url);
        }
        return new LinkResponse(linkId, url);
    }

    @Override
    @Transactional
    public ListLinksResponse listAll(long chatId) throws ChatNotFoundException {
        long tgChatId = tgChatRepository.findByChatId(chatId).getId();
        LinkResponse[] linkResponses = chatLinkRepository.findByTgChatId(tgChatId)
            .stream()
            .map(chatLink -> linkRepository.findById(chatLink.linkId())
                .get())
            .map(link -> new LinkResponse(link.getId(), link.getUri()))
            .toArray(LinkResponse[]::new);
        return new ListLinksResponse(linkResponses, (long) linkResponses.length);
    }

    @Override
    @Transactional
    public long update(long id, OffsetDateTime updatedAt, OffsetDateTime checkedAt) {
        return linkRepository.update(id, updatedAt, checkedAt);
    }

    @Override
    @Transactional
    public long[] getChatIdsByLinkId(long linkId) {
        return chatLinkRepository.findByLinkId(linkId)
            .stream()
            .map(chatLink -> tgChatRepository.findById(chatLink.tgChatId()))
            .filter(Optional::isPresent)
            .mapToLong(chat -> chat.get().getChatId()).toArray();
    }

    @Override
    @Transactional
    public List<Link> getStaleLinks(long limit) {
        return linkRepository.findStaleLinks(limit);
    }

}
