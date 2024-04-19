package edu.java.service.jpa;

import edu.java.controller.dto.LinkResponse;
import edu.java.controller.dto.ListLinksResponse;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.controller.exception.LinkAlreadyExistException;
import edu.java.controller.exception.LinkNotFoundException;
import edu.java.domain.GithubLink;
import edu.java.domain.GithubLinkEntity;
import edu.java.domain.LinkEntity;
import edu.java.domain.StackOverflowLink;
import edu.java.domain.StackOverflowLinkEntity;
import edu.java.domain.TgChatEntity;
import edu.java.repository.jpa.JpaGithubLinkRepository;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.repository.jpa.JpaStackOverflowLinkRepository;
import edu.java.repository.jpa.JpaTgChatRepository;
import edu.java.scheduler.dto.UpdateType;
import edu.java.service.LinkService;
import edu.java.service.util.LinkTypeService;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JpaLinkService implements LinkService {
    private final JpaTgChatRepository tgChatRepository;
    private final JpaLinkRepository linkRepository;
    private final LinkTypeService linkTypeService;
    private final JpaStackOverflowLinkRepository stackOverflowLinkRepository;
    private final JpaGithubLinkRepository githubLinkRepository;

    @Override
    @Transactional
    public LinkResponse add(long tgChatId, URI url) throws ChatNotFoundException, LinkAlreadyExistException {
        LinkEntity link = linkRepository.findByUri(url)
                                        .orElse(new LinkEntity().setUri(url)
                                                                .setLinkType(linkTypeService.getType(url.getHost()))
                                                                .setUpdatedAt(OffsetDateTime.now())
                                                                .setCheckedAt(OffsetDateTime.now())
                                                                .setTgChats(new HashSet<>()));
        TgChatEntity tgChat = tgChatRepository.findByChatId(tgChatId).orElseThrow(ChatNotFoundException::new);
        if (link.getTgChats().contains(tgChat)) {
            throw new LinkAlreadyExistException();
        }
        tgChat.addLink(link);
        tgChatRepository.saveAndFlush(tgChat);
        long id = linkRepository.saveAndFlush(link).getId();
        return new LinkResponse(id, link.getUri());
    }

    @Override
    @Transactional
    public LinkResponse remove(long tgChatId, URI url) throws ChatNotFoundException, LinkNotFoundException {
        TgChatEntity tgChat = tgChatRepository.findByChatId(tgChatId).orElseThrow(ChatNotFoundException::new);
        LinkEntity link = linkRepository.findByUri(url).orElseThrow(LinkNotFoundException::new);
        if (tgChat.getLinks().contains(link)) {
            tgChat.removeLink(link);
            if (link.getTgChats().isEmpty()) {
                linkRepository.deleteById(link.getId());
            }
            tgChatRepository.saveAndFlush(tgChat);
            return new LinkResponse(link.getId(), link.getUri());
        }
        throw new LinkNotFoundException();
    }

    @Override
    @Transactional
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
    @Transactional
    public long[] getChatIdsByLinkId(long linkId) {
        Optional<LinkEntity> link = linkRepository.findById(linkId);
        if (link.isEmpty()) {
            return new long[] {};
        }
        return link.get().getTgChats().stream().mapToLong(TgChatEntity::getChatId).toArray();
    }

    @Override
    @Transactional
    public long update(long id, OffsetDateTime updatedAt, OffsetDateTime checkedAt) {
        if (linkRepository.existsById(id)) {
            LinkEntity link = linkRepository.findById(id).get().setUpdatedAt(updatedAt).setCheckedAt(checkedAt);
            linkRepository.saveAndFlush(link);
            return 1L;
        }
        return 0L;
    }

    @Override
    @Transactional
    public List<LinkEntity> getStaleLinks(long limit) {
        return linkRepository.findStaleLinks(limit);
    }

    @Override
    @Transactional
    public UpdateType updateStackOverflowAnswerCount(StackOverflowLink stackOverflowLink) {
        StackOverflowLinkEntity link =
            stackOverflowLinkRepository.findByLinkId(stackOverflowLink.linkId())
                                       .orElse(new StackOverflowLinkEntity().setAnswerCount(
                                           stackOverflowLink.answerCount()).setLink(linkRepository.findById(
                                           stackOverflowLink.linkId()).get()));
        long prevAnswerCount = link.getAnswerCount();
        stackOverflowLinkRepository.saveAndFlush(link);
        return prevAnswerCount != stackOverflowLink.answerCount() ? UpdateType.UPDATE_ANSWER : UpdateType.NO_UPDATE;
    }

    @Override
    @Transactional
    public UpdateType updateGithubBranchCount(GithubLink githubLink) {
        GithubLinkEntity link = githubLinkRepository.findByLinkId(githubLink.linkId())
                                                    .orElse(new GithubLinkEntity().setBranchCount(
                                                                                      githubLink.branchCount())
                                                                                  .setLink(linkRepository.findById(
                                                                                      githubLink.linkId()).get()));
        long prevBranchCount = link.getBranchCount();
        githubLinkRepository.saveAndFlush(link);
        return prevBranchCount != githubLink.branchCount() ? UpdateType.UPDATE_BRANCH : UpdateType.NO_UPDATE;
    }
}
