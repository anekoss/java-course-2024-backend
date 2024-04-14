package edu.java.scrapper.service.service;

import edu.java.controller.dto.LinkResponse;
import edu.java.controller.dto.ListLinksResponse;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.controller.exception.LinkAlreadyExistException;
import edu.java.controller.exception.LinkNotFoundException;
import edu.java.domain.ChatLink;
import edu.java.domain.GithubLink;
import edu.java.domain.LinkEntity;
import edu.java.domain.LinkType;
import edu.java.domain.StackOverflowLink;
import edu.java.domain.TgChatEntity;
import edu.java.repository.ChatLinkRepository;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import edu.java.scheduler.dto.UpdateType;
import edu.java.scrapper.IntegrationTest;
import edu.java.service.LinkService;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import static edu.java.domain.LinkType.STACKOVERFLOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AllArgsConstructor
public abstract class LinkServiceTest extends IntegrationTest {

    private LinkRepository linkRepository;
    private ChatLinkRepository chatLinkRepository;
    private TgChatRepository tgChatRepository;
    private LinkService linkService;

    @Test
    @Rollback
    @Transactional
    void testAdd_shouldThrowExceptionIfChatNoExist() {
        assertThrows(
            ChatNotFoundException.class,
            () -> linkService.add(223L, URI.create("https://stackoverflow.com/"))
        );
    }

    @Test
    @Rollback
    @Transactional
    void testAdd_shouldThrowExceptionIfLinkAlreadyExistByChat() {
        assertThrows(
            LinkAlreadyExistException.class,
            () -> linkService.add(327034L, URI.create("https://github.com/anekoss/tinkoff"))
        );
    }

    @Test
    @Rollback
    @Transactional
    void testAdd_shouldCorrectlyAddLinkByChatIfTableHaveSame() throws ChatNotFoundException, LinkAlreadyExistException {
        URI uri = URI.create("https://github.com/anekoss/tinkoff-project");
        LinkResponse response = linkService.add(555555L, uri);
        LinkEntity link = linkRepository.findByUri(uri).get();
        assertEquals(response, new LinkResponse(link.getId(), uri));
        TgChatEntity tgChat = tgChatRepository.findByChatId(555555L);
        assertThat(chatLinkRepository.findByTgChatId(tgChat.getId())).contains(new ChatLink(
            tgChat.getId(),
            link.getId()
        ));
    }

    @Test
    @Rollback
    @Transactional
    void testAdd_shouldCorrectlyAddLinkToChatIfTableHaveNotSame()
        throws ChatNotFoundException, LinkAlreadyExistException {
        URI uri = URI.create("https://github.com/anekoss/tinkoff-project");
        LinkResponse response = linkService.add(555555L, uri);
        LinkEntity link = linkRepository.findByUri(uri).get();
        assertEquals(response, new LinkResponse(link.getId(), uri));
        TgChatEntity tgChat = tgChatRepository.findByChatId(555555L);
        assertThat(chatLinkRepository.findByTgChatId(tgChat.getId())).contains(new ChatLink(
            tgChat.getId(),
            link.getId()
        ));
    }

    @Test
    @Rollback
    @Transactional
    void testRemove_shouldThrowExceptionIfChatNoExist() {
        assertThrows(
            ChatNotFoundException.class,
            () -> linkService.remove(333L, URI.create("https://stackoverflow.com/"))
        );
    }

    @Test
    @Rollback
    @Transactional
    void testRemove_shouldThrowExceptionIfChatHaveNotLink() {
        URI uri = URI.create("https://github.com");
        assertThrows(
            LinkNotFoundException.class,
            () -> linkService.remove(555555L, uri)
        );
    }

    @Test
    @Rollback
    @Transactional
    void testRemove_shouldRemoveLinkFromTableIfLinkHaveOnlyOneChat()
        throws ChatNotFoundException, LinkNotFoundException {
        URI uri = URI.create("https://stackoverflow.com/questions/44760112/marching-cubes-generating-holes-in-mesh");
        linkService.remove(124025L, uri);
        Optional<LinkEntity> link =  linkRepository.findByUri(uri);
        assert link.isEmpty();
    }

    @Test
    @Rollback
    @Transactional
    void testRemove_shouldNotRemoveLinkFromTableIfLinkHaveManyChat()
        throws ChatNotFoundException, LinkNotFoundException {
        URI uri = URI.create("https://github.com/anekoss/tinkoff");
        linkService.remove(444444L, uri);
        assert linkRepository.findByUri(uri).isPresent();
    }

    @Test
    @Rollback
    @Transactional
    void testFindAll_shouldReturnLinksIfChatHaveLinks() throws ChatNotFoundException {
        ListLinksResponse links = linkService.listAll(327034L);
        assert links.size() == 2;
        List<String> uris = Arrays.stream(links.linkResponses()).map(link -> link.uri().toString()).toList();
        assertThat(uris).contains("https://github.com/anekoss/tinkoff");
        assertThat(uris).contains(
            "https://stackoverflow.com/questions/59339862/retrieving-text-body-of-answers-and-comments-using-stackexchange-api");
    }

    @Test
    @Rollback
    @Transactional
    void testFindAll_shouldReturnEmptyListLinksResponseIfChatHaveNotLinks() throws ChatNotFoundException {
        ListLinksResponse response = linkService.listAll(555555L);
        assert response.size() == 0;
        assert response.linkResponses().length == 0;
    }

    @Test
    @Rollback
    @Transactional
    void testGetChatIdsByLinkId_shouldCorrectlyReturnIds() {
        long[] chatIds = linkService.getChatIdsByLinkId(1L);
        assert chatIds.length == 3;
        assertThat(chatIds).contains(124025L, 327034L, 444444L);
    }

    @Test
    @Rollback
    @Transactional
    void testGetChatIdsByLinkId_shouldReturnEmptyArrayIfNoChats() {
        long linkId = linkRepository.add(new LinkEntity().setUri(URI.create("https://github.com/"))
                                                         .setLinkType(LinkType.GITHUB)
                                                         .setUpdatedAt(OffsetDateTime.now())
                                                         .setCheckedAt(OffsetDateTime.now()));
        long[] chatIds = linkService.getChatIdsByLinkId(linkId);
        assert chatIds.length == 0;
    }

    @Test
    @Rollback
    @Transactional
    void testGetChatIdsByLinkId_shouldReturnEmptyArrayIfNoLink() {
        long[] chatIds = linkService.getChatIdsByLinkId(3456L);
        assert chatIds.length == 0;
    }

    @Test
    @Transactional
    @Rollback
    void testUpdate_shouldReturnZeroIfNoLink() {
        assertThat(linkRepository.update(10L, OffsetDateTime.now(), OffsetDateTime.now())).isEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback
    void testUpdate_shouldCorrectlyUpdateExistLink() {
        OffsetDateTime checked = OffsetDateTime.now();
        assertThat(linkRepository.update(
            1L,
            checked,
            checked
        )).isEqualTo(1);
        LinkEntity actual = linkRepository.findById(1L).get();
        assertThat(actual.getUpdatedAt()).isEqualToIgnoringNanos(checked);
        assertThat(actual.getCheckedAt()).isEqualToIgnoringNanos(checked);
    }

    @Test
    @Transactional
    @Rollback
    void testGetStaleLinks_shouldCorrectlyReturnLinks() {
        LinkEntity link = new LinkEntity().setUri(URI.create("https://stackoverflow.com/"))
                                          .setUpdatedAt(OffsetDateTime.now())
                                          .setLinkType(STACKOVERFLOW)
                                          .setCheckedAt(OffsetDateTime.of(1900, 12, 22, 13, 23, 34, 4, ZoneOffset.UTC));
        long linkId = linkRepository.add(link);
        link.setId(linkId);
        List<LinkEntity> links = linkService.getStaleLinks(1L);
        assert links.size() == 1;
        assertEquals(links.getFirst().getUri().toString(), link.getUri().toString());
    }

    @Test
    @Transactional
    @Rollback
    void testGetStaleLinks_shouldReturnEmptyListIfNoList() throws LinkNotFoundException {
        List<LinkEntity> links = linkRepository.findAll();
        for (LinkEntity link : links) {
            linkRepository.remove(link.getUri());
        }
        assert linkRepository.findAll().isEmpty();
        assert linkRepository.findStaleLinks(1L).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateStackOverflowAnswerCount_shouldReturnUpdateAnswerIfNewGreater() {
        assert linkService.updateStackOverflowAnswerCount(new StackOverflowLink(2L, 7L)) == UpdateType.UPDATE_ANSWER;
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateStackOverflowAnswerCount_shouldNoUpdateAnswerIfNewEq() {
        assert linkService.updateStackOverflowAnswerCount(new StackOverflowLink(2L, 3L)) == UpdateType.NO_UPDATE;
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateGithubBranchCount_shouldReturnUpdateBranchIfNewNoEq() {
        assert linkService.updateGithubBranchCount(new GithubLink(1L, 7L)) == UpdateType.UPDATE_BRANCH;
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateGithubBranchCount_shouldReturnNoUpdateIfNewLess() {
        assert linkService.updateGithubBranchCount(new GithubLink(1L, 7L)) == UpdateType.UPDATE_BRANCH;
    }
}
