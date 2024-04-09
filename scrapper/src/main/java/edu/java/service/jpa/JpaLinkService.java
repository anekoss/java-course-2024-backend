package edu.java.service.jpa;

import edu.java.controller.dto.LinkResponse;
import edu.java.controller.dto.ListLinksResponse;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.controller.exception.LinkAlreadyExistException;
import edu.java.controller.exception.LinkNotFoundException;
import edu.java.domain.GithubLink;
import edu.java.domain.Link;
import edu.java.domain.StackOverflowLink;
import edu.java.domain.TgChat;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.repository.jpa.JpaTgChatRepository;
import edu.java.scheduler.dto.UpdateType;
import edu.java.service.LinkService;
import edu.java.service.util.LinkTypeService;
import lombok.AllArgsConstructor;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class JpaLinkService implements LinkService {
    private final JpaTgChatRepository tgChatRepository;
    private final JpaLinkRepository linkRepository;
    private final LinkTypeService linkTypeService;

    @Override
    public LinkResponse add(long tgChatId, URI url) throws ChatNotFoundException, LinkAlreadyExistException {
        Link link = linkRepository.findByUri(url).orElse(new Link().setUri(url)
                                                                   .setLinkType(linkTypeService.getType(url.getHost()))
                                                                   .setUpdatedAt(OffsetDateTime.now())
                                                                   .setCheckedAt(OffsetDateTime.now())
                                                                   .setTgChats(new HashSet<>()));
        TgChat tgChat = tgChatRepository.findByChatId(tgChatId).orElseThrow(ChatNotFoundException::new);
        if (link.getTgChats().contains(tgChat)) {
            throw new LinkAlreadyExistException();
        }
        tgChat.addLink(link);
        tgChatRepository.saveAndFlush(tgChat);
        link = linkRepository.saveAndFlush(link);
        return new LinkResponse(link.getId(), link.getUri());
    }

    @Override
    public LinkResponse remove(long tgChatId, URI url) throws ChatNotFoundException, LinkNotFoundException {
        Link link = linkRepository.findByUri(url).orElseThrow(LinkNotFoundException::new);
        TgChat tgChat = tgChatRepository.findByChatId(tgChatId).orElseThrow(ChatNotFoundException::new);
        tgChat.removeLink(link);
        tgChatRepository.saveAndFlush(tgChat);
        if (link.getTgChats().isEmpty()) {
            linkRepository.delete(link);
        }
        return new LinkResponse(link.getId(), link.getUri());
    }

    @Override
    public ListLinksResponse listAll(long tgChatId) throws ChatNotFoundException {
        LinkResponse[] linkResponses =
                tgChatRepository.findByChatId(tgChatId)
                                .orElseThrow(ChatNotFoundException::new)
                                .getLinks()
                                .stream()
                                .map(link -> new LinkResponse(link.getId(), link.getUri()))
                                .toArray(LinkResponse[]::new);
        return new ListLinksResponse(linkResponses, (long) linkResponses.length);
    }

    @Override
    public long[] getChatIdsByLinkId(long linkId) {
        Optional<Link> link = linkRepository.findById(linkId);
        if (link.isEmpty()) {
            return new long[]{};
        }
        return link.get().getTgChats().stream().mapToLong(TgChat::getChatId).toArray();
    }

    @Override
    public long update(long id, OffsetDateTime updatedAt, OffsetDateTime checkedAt) {
        return linkRepository.updateById(id, updatedAt, checkedAt);
    }


    @Override
    public List<Link> getStaleLinks(long limit) {
        return linkRepository.findStaleLinks(limit);
    }

    @Override
    public UpdateType updateStackOverflowAnswerCount(StackOverflowLink stackOverflowLink) {
        return null;
    }

    @Override
    public UpdateType updateGithubBranchCount(GithubLink githubLink) {
        return null;
    }
}
