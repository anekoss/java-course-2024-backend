package edu.java.scrapper.repository.jdbc;

import edu.java.domain.TgChat;
import edu.java.repository.jdbc.JdbcTgChatRepository;
import edu.java.scrapper.IntegrationTest;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import static edu.java.domain.LinkType.GITHUB;
import static edu.java.domain.LinkType.STACKOVERFLOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class JdbcChatRepositoryTest extends IntegrationTest {
    @Autowired
    private JdbcTgChatRepository tgChatRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    @Rollback
    void testAddNoExistChat() {
        TgChat tgChat = new TgChat(214L);
        assertThat(tgChatRepository.save(tgChat)).isEqualTo(1);
        TgChat actual = jdbcTemplate.queryForObject("select * from tg_chats where chat_id = ?",
            new BeanPropertyRowMapper<>(TgChat.class), 214L
        );
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getChatId()).isEqualTo(214L);
    }

    @Test
    @Transactional
    @Rollback
    void testAddExistChat() {
        TgChat tgChat = new TgChat(214L);
        assertThat(tgChatRepository.save(tgChat)).isEqualTo(1);
        assertThrows(DuplicateKeyException.class, () -> tgChatRepository.save(tgChat));
    }

    @Test
    @Transactional
    @Rollback
    void testAddChatWithNullChatId() {
        TgChat tgChat = new TgChat();
        assertThrows(DataIntegrityViolationException.class, () -> tgChatRepository.save(tgChat));
    }

    @Test
    @Transactional
    @Rollback
    void testRemoveExistChatWithoutLink() {
        jdbcTemplate.update("insert into tg_chats(chat_id) values (?)", 210L);
        TgChat tgChat = jdbcTemplate.queryForObject("select * from tg_chats where chat_id = ?",
            new BeanPropertyRowMapper<>(TgChat.class), 210L
        );
        assertThat(tgChat).isNotNull();
        assertThat(tgChat.getId()).isNotNull();
        tgChatRepository.delete(tgChat);
        assertThrows(
            EmptyResultDataAccessException.class,
            () -> jdbcTemplate.queryForObject("select * from tg_chats where chat_id = ?",
                new BeanPropertyRowMapper<>(TgChat.class), 210L
            )
        );
    }

    @Test
    @Transactional
    @Rollback
    void testRemoveNoExistChat() {
        TgChat tgChat = new TgChat();
        tgChat.setId(1L);
        tgChat.setChatId(250L);
        assertThat(tgChatRepository.delete(tgChat)).isEqualTo(0);
    }

    void initData() {
        jdbcTemplate.update("insert into tg_chats(chat_id) values (?)", 210L);
        jdbcTemplate.update("insert into tg_chats(chat_id) values (?)", 153L);
        jdbcTemplate.update(
            "insert into links(uri, type, updated_at, checked_at) values(?, ?, ?, ?)",
            "https://github.com/anekoss/tinkoff-project",
            GITHUB.toString(),
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
        jdbcTemplate.update(
            "insert into links(uri, type, updated_at, checked_at) values(?, ?, ?, ?)",
            "https://stackoverflow.com/",
            STACKOVERFLOW.toString(),
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
        Long tgChatId = jdbcTemplate.queryForObject("select id from tg_chats where chat_id = ?", Long.class, 210L);
        Long githubLinkId = jdbcTemplate.queryForObject(
            "select id from links where uri = ?",
            Long.class,
            "https://github.com/anekoss/tinkoff-project"
        );
        Long stackOverflowLinkId =
            jdbcTemplate.queryForObject("select id from links where uri = ?", Long.class, "https://stackoverflow.com/");
        jdbcTemplate.update("insert into tg_chat_links(tg_chat_id, link_id) values (?, ?)", tgChatId, githubLinkId);
        jdbcTemplate.update(
            "insert into tg_chat_links(tg_chat_id, link_id) values (?, ?)",
            tgChatId,
            stackOverflowLinkId
        );
        Long tgChatId2 = jdbcTemplate.queryForObject("select id from tg_chats where chat_id = ?", Long.class, 153L);
        jdbcTemplate.update(
            "insert into tg_chat_links(tg_chat_id, link_id) values (?, ?)",
            tgChatId2,
            stackOverflowLinkId
        );
    }

    @Test
    @Transactional
    @Rollback
    void testRemoveExistChatWithLink() {
        initData();
        TgChat tgChat = jdbcTemplate.queryForObject("select * from tg_chats where chat_id = ?",
            new BeanPropertyRowMapper<>(TgChat.class), 210L
        );
        Long countLinks = jdbcTemplate.queryForObject(
            "select count(*) from tg_chat_links where tg_chat_id = ?",
            Long.class,
            tgChat.getId()
        );
        assertThat(countLinks).isEqualTo(2L);
        tgChatRepository.delete(tgChat);
        assertThrows(
            EmptyResultDataAccessException.class,
            () -> jdbcTemplate.queryForObject("select * from tg_chats where chat_id = ?",
                new BeanPropertyRowMapper<>(TgChat.class), 210L
            )
        );
        assertThat(jdbcTemplate.queryForObject(
            "select count(*) from tg_chat_links where tg_chat_id = ?",
            Long.class,
            tgChat.getId()
        )).isEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback
    void testFindAllWithChats() {
        initData();
        Long count = jdbcTemplate.queryForObject("select count(*) from tg_chats", Long.class);
        assertThat(count).isEqualTo(2L);
        List<TgChat> chats = tgChatRepository.findAll();
        assertThat((long) chats.size()).isEqualTo(count);
        long[] chatIds = chats.stream().mapToLong(TgChat::getChatId).toArray();
        assertThat(chatIds).contains(210L);
        assertThat(chatIds).contains(153L);
    }

    @Test
    @Transactional
    @Rollback
    void testFindAllWithoutChats() {
        Long count = jdbcTemplate.queryForObject("select count(*) from tg_chats", Long.class);
        assertThat(count).isEqualTo(0L);
        List<TgChat> chats = tgChatRepository.findAll();
        assertThat((long) chats.size()).isEqualTo(count);
    }

    @Test
    @Transactional
    @Rollback
    void testFindByChatIdWithChat() {
        initData();
        Optional<TgChat> tgChatOptional = tgChatRepository.findByChatId(210L);
        assertThat(tgChatOptional).isPresent();
        assertThat(tgChatOptional.get().getId()).isNotNull().isGreaterThan(0L);
        assertThat(tgChatOptional.get().getChatId()).isEqualTo(210L);
    }

    @Test
    @Transactional
    @Rollback
    void testFindByChatIdWithoutChat() {
        Optional<TgChat> tgChatOptional = tgChatRepository.findByChatId(210L);
        assertThat(tgChatOptional).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void testFindChatIdsByLinkIdWithChatIdAndLinkId() {
        initData();
        Long linkId =
            jdbcTemplate.queryForObject("select id from links where uri = ?", Long.class, "https://stackoverflow.com/");
        List<Long> chatIds = tgChatRepository.findChatIdsByLinkId(linkId);
        assertThat(chatIds.size()).isEqualTo(2);
        assertThat(chatIds).contains(153L);
        assertThat(chatIds).contains(210L);
    }

    @Test
    @Transactional
    @Rollback
    void testFindChatIdsByLinkIdWithChatIdAndWithoutLinkId() {
        initData();
        List<Long> chatIds = tgChatRepository.findChatIdsByLinkId(277L);
        assertThat(chatIds.size()).isEqualTo(0L);
    }

    @Test
    @Transactional
    @Rollback
    void testFindChatIdsByLinkIdWithoutChatIdAndWithLinkId() {
        jdbcTemplate.update(
            "insert into links(uri, type, updated_at, checked_at) values(?, ?, ?, ?)",
            "https://stackoverflow.com/",
            STACKOVERFLOW.toString(),
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
        Long linkId =
            jdbcTemplate.queryForObject("select id from links where uri = ?", Long.class, "https://stackoverflow.com/");
        List<Long> chatIds = tgChatRepository.findChatIdsByLinkId(linkId);
        assertThat(chatIds.size()).isEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback
    void testFindChatIdsByLinkIdWithoutChatIdAndWithoutLinkId() {
        List<Long> chatIds = tgChatRepository.findChatIdsByLinkId(177L);
        assertThat(chatIds.size()).isEqualTo(0);
    }
}
