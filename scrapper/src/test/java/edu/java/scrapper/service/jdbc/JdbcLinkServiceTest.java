package edu.java.scrapper.service.jdbc;

import edu.java.controller.dto.LinkResponse;
import edu.java.controller.dto.ListLinksResponse;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.controller.exception.LinkAlreadyExistException;
import edu.java.controller.exception.LinkNotFoundException;
import edu.java.domain.ChatLink;
import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.domain.TgChat;
import edu.java.repository.jdbc.JdbcChatLinkRepository;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.JdbcTgChatRepository;
import edu.java.scrapper.IntegrationTest;
import edu.java.service.jdbc.JdbcLinkService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.net.URI;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class JdbcLinkServiceTest extends IntegrationTest {
    @Autowired
    private JdbcLinkService linkService;
    @Autowired
    private JdbcLinkRepository linkRepository;
    @Autowired
    private JdbcChatLinkRepository chatLinkRepository;
    @Autowired
    private JdbcTgChatRepository tgChatRepository;

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
        Link link = linkRepository.findByUri(uri).get();
        assertEquals(response, new LinkResponse(link.getId(), uri));
        TgChat tgChat = tgChatRepository.findByChatId(555555L);
        assertThat(chatLinkRepository.findByTgChatId(tgChat.getId())).contains(new ChatLink(tgChat.getId(), link.getId()));
    }

    @Test
    @Rollback
    @Transactional
    void testAdd_shouldCorrectlyAddLinkToChatIfTableHaveNotSame() throws ChatNotFoundException, LinkAlreadyExistException {
        URI uri = URI.create("https://github.com/anekoss/tinkoff-project");
        LinkResponse response = linkService.add(555555L, uri);
        Link link = linkRepository.findByUri(uri).get();
        assertEquals(response, new LinkResponse(link.getId(), uri));
        TgChat tgChat = tgChatRepository.findByChatId(555555L);
        assertThat(chatLinkRepository.findByTgChatId(tgChat.getId())).contains(new ChatLink(tgChat.getId(), link.getId()));
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
    void testRemove_shouldRemoveLinkFromTableIfLinkHaveOnlyOneChat() throws ChatNotFoundException, LinkNotFoundException {
        URI uri = URI.create("https://stackoverflow.com/questions/44760112/marching-cubes-generating-holes-in-mesh");
        linkService.remove(124025L, uri);
        assert linkRepository.findByUri(uri).isEmpty();
    }

    @Test
    @Rollback
    @Transactional
    void testRemove_shouldNotRemoveLinkFromTableIfLinkHaveManyChat() throws ChatNotFoundException, LinkNotFoundException {
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
        assertEquals(links.linkResponses()[0].url().toString(), "https://github.com/anekoss/tinkoff");
        assertEquals(links.linkResponses()[1]
                        .url()
                        .toString(),
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
        long[] chatIds = linkService.getChatIdsByLinkId(1);
        assert chatIds.length == 3;
        assert chatIds[0] == 124025L;
        assert chatIds[1] == 327034L;
        assert chatIds[2] == 444444L;
    }

    @Test
    @Rollback
    @Transactional
    void testGetChatIdsByLinkId_shouldReturnEmptyArrayIfNoChats() {
        long linkId = linkRepository.add(new Link().setUri(URI.create("https://github.com/"))
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
        Link actual = linkRepository.findById(1L).get();
        assertThat(actual.getUpdatedAt()).isEqualToIgnoringNanos(checked);
        assertThat(actual.getCheckedAt()).isEqualToIgnoringNanos(checked);
    }

}
