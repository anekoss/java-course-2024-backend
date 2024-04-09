package edu.java.scrapper.repository.jdbc;

import edu.java.controller.exception.LinkAlreadyExistException;
import edu.java.controller.exception.LinkNotFoundException;
import edu.java.domain.ChatLink;
import edu.java.repository.jdbc.JdbcChatLinkRepository;
import edu.java.scrapper.IntegrationTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class JdbcChatLinkRepositoryTest extends IntegrationTest {
    @Autowired
    private JdbcChatLinkRepository chatLinkRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    @Rollback
    void testAdd_shouldCorrectlyAddChatAndLink() throws LinkAlreadyExistException {
        ChatLink chatLink = new ChatLink(2L, 3L);
        long id = chatLinkRepository.add(chatLink);
        assert id > 0;
        Long actual = jdbcTemplate.queryForObject(
                "select id from tg_chat_links where tg_chat_id = ? and link_id = ?",
                Long.class, 2L, 3L
        );
        assert actual != null;
        assert actual == id;
    }

    @Test
    @Transactional
    @Rollback
    void testAdd_shouldThrowExceptionIfChatAlreadyTrackLink() {
        ChatLink chatLink = new ChatLink(1L, 1L);
        assertThrows(LinkAlreadyExistException.class, () -> chatLinkRepository.add(chatLink));
    }

    @Test
    @Transactional
    @Rollback
    void testRemove_shouldCorrectlyRemoveLinkFromChat() throws LinkNotFoundException {
        ChatLink chatLink = new ChatLink(1L, 1L);
        long id = chatLinkRepository.remove(chatLink);
        assert id > 0;
        Long actual = jdbcTemplate.queryForObject(
                "select count(*) from tg_chat_links where tg_chat_id = ? and link_id = ?",
                Long.class, 1L, 1L
        );
        assert actual != null;
        assert actual == 0L;
    }

    @Test
    @Transactional
    @Rollback
    void testRemove_shouldThrowsExceptionIfNoChatLink() {
        ChatLink chatLink = new ChatLink(1L, 10L);
        assertThrows(LinkNotFoundException.class, () -> chatLinkRepository.remove(chatLink));
    }

    @Test
    @Transactional
    @Rollback
    void testFindByTgChatId_shouldCorrectlyReturnChatLinkList() {
        List<ChatLink> chatLinks = chatLinkRepository.findByTgChatId(1L);
        assert chatLinks.size() == 3;
        assert chatLinks.getFirst().equals(new ChatLink(1L, 1L));
        assert chatLinks.getLast().equals(new ChatLink(1L, 3L));
    }

    @Test
    @Transactional
    @Rollback
    void testFindByTgChatId_shouldCorrectlyReturnEmptyListIfNoChatLinks() {
        List<ChatLink> chatLinks = chatLinkRepository.findByTgChatId(4L);
        assert chatLinks.isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void testFindByLinkId_shouldCorrectlyReturnChatLinkList() {
        List<ChatLink> chatLinks = chatLinkRepository.findByLinkId(1L);
        assert chatLinks.size() == 3;
        assert chatLinks.getFirst().equals(new ChatLink(1L, 1L));
        assert chatLinks.getLast().equals(new ChatLink(3L, 1L));
    }

    @Test
    @Transactional
    @Rollback
    void testFindByLinkId_shouldCorrectlyReturnEmptyListIfNoChatLinks() {
        List<ChatLink> chatLinks = chatLinkRepository.findByLinkId(10L);
        assert chatLinks.isEmpty();
    }
}
