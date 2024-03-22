package edu.java.scrapper.repository.jdbc;

import edu.java.domain.Link;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.IntegrationTest;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import static edu.java.domain.LinkType.GITHUB;
import static edu.java.domain.LinkType.STACKOVERFLOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class JdbcLinkRepositoryTest extends IntegrationTest {

    @Autowired
    private JdbcLinkRepository linkRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    void initChats() {
        jdbcTemplate.update("insert into tg_chats(chat_id) values (?)", 210L);
        jdbcTemplate.update("insert into tg_chats(chat_id) values (?)", 153L);
    }

    @Test
    @Transactional
    @Rollback
    void testSaveNotExistLink() throws URISyntaxException {
        initChats();
        Link link = new Link(new URI("https://stackoverflow.com/"), STACKOVERFLOW);
        Long chatId = jdbcTemplate.queryForObject("select id from tg_chats where chat_id = ?", Long.class, 210L);
        assertEquals(linkRepository.save(chatId, link), 1);
        Link actualLink = jdbcTemplate.queryForObject("select * from links where uri = ?",
            new BeanPropertyRowMapper<>(Link.class), "https://stackoverflow.com/"
        );
        assertThat(actualLink).isNotNull();
        assertThat(actualLink.getId()).isGreaterThan(0L);
        assertThat(actualLink.getUri()).isEqualTo(link.getUri());
        assertThat(actualLink.getType()).isEqualTo(link.getType());
        assertThat(actualLink.getCheckedAt()).isEqualToIgnoringNanos(link.getCheckedAt());
        assertThat(actualLink.getUpdatedAt()).isEqualToIgnoringNanos(link.getUpdatedAt());
        Long chatLinkId = jdbcTemplate.queryForObject(
            "select id from tg_chat_links where tg_chat_id = ? and link_id = ?",
            Long.class,
            chatId,
            actualLink.getId()
        );
        assertThat(chatLinkId).isGreaterThan(0L);
    }

    @Test
    @Transactional
    @Rollback
    void testSaveExistLink() throws URISyntaxException {
        initChats();
        Link link = new Link(new URI("https://stackoverflow.com/"), STACKOVERFLOW);
        Long chatId = jdbcTemplate.queryForObject("select id from tg_chats where chat_id = ?", Long.class, 210L);
        linkRepository.save(chatId, link);
        assertThrows(DuplicateKeyException.class, () -> linkRepository.save(chatId, link));
    }

    @Test
    @Transactional
    @Rollback
    void testDeleteNoExistLink() {
        initChats();
        Long chatId = jdbcTemplate.queryForObject("select id from tg_chats where chat_id = ?", Long.class, 210L);
        assertThat(linkRepository.delete(chatId, URI.create("https://stackoverflow.com/"))).isEqualTo(0);
    }

    void initLink() {
        Long chatId1 = jdbcTemplate.queryForObject("select id from tg_chats where chat_id = ?", Long.class, 210L);
        Long chatId2 = jdbcTemplate.queryForObject("select id from tg_chats where chat_id = ?", Long.class, 153L);
        jdbcTemplate.update(
            "insert into links(uri, type, updated_at, checked_at) values (?, ?, ?, ?)",
            "https://stackoverflow.com/",
            STACKOVERFLOW.toString(),
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
        jdbcTemplate.update(
            "insert into links(uri, type, updated_at, checked_at) values(?, ?, ?, ?)",
            "https://github.com/anekoss/tinkoff-project",
            GITHUB.toString(),
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
        Long githubLinkId = jdbcTemplate.queryForObject(
            "select id from links where uri = ?",
            Long.class,
            "https://github.com/anekoss/tinkoff-project"
        );
        Long stackOverflowLinkId =
            jdbcTemplate.queryForObject("select id from links where uri = ?", Long.class, "https://stackoverflow.com/");
        jdbcTemplate.update(
            "insert into tg_chat_links(tg_chat_id, link_id) values (?, ?)",
            chatId1,
            stackOverflowLinkId
        );
        jdbcTemplate.update(
            "insert into tg_chat_links(tg_chat_id, link_id) values (?, ?)",
            chatId2,
            stackOverflowLinkId
        );
        jdbcTemplate.update("insert into tg_chat_links(tg_chat_id, link_id) values (?, ?)", chatId2, githubLinkId);
    }

    @Test
    @Transactional
    @Rollback
    void testDeleteExistLinkHaveManyChats() throws URISyntaxException {
        initChats();
        initLink();
        Long chatId1 = jdbcTemplate.queryForObject("select id from tg_chats where chat_id = ?", Long.class, 210L);
        linkRepository.delete(chatId1, new URI("https://stackoverflow.com/"));
        Long chatLinkCount1 =
            jdbcTemplate.queryForObject("select count(*) from tg_chat_links where tg_chat_id = ?", Long.class, chatId1);
        assertEquals(chatLinkCount1, 0L);
        Long chatId2 = jdbcTemplate.queryForObject("select id from tg_chats where chat_id = ?", Long.class, 153L);
        Long chatLinkCount2 =
            jdbcTemplate.queryForObject("select count(*) from tg_chat_links where tg_chat_id = ?", Long.class, chatId2);
        assertEquals(chatLinkCount2, 2L);
        Long linkCount = jdbcTemplate.queryForObject(
            "select count(*) from links where uri = ?",
            Long.class,
            "https://stackoverflow.com/"
        );
        assertEquals(linkCount, 1L);
    }

    @Test
    @Transactional
    @Rollback
    void testDeleteExistLinkHaveOneChats() throws URISyntaxException {
        initChats();
        initLink();
        Long chatId1 = jdbcTemplate.queryForObject("select id from tg_chats where chat_id = ?", Long.class, 210L);
        Long chatId2 = jdbcTemplate.queryForObject("select id from tg_chats where chat_id = ?", Long.class, 153L);
        linkRepository.delete(chatId1, new URI("https://stackoverflow.com/"));
        linkRepository.delete(chatId2, new URI("https://stackoverflow.com/"));
        Long chatLinkCount1 =
            jdbcTemplate.queryForObject("select count(*) from tg_chat_links where tg_chat_id = ?", Long.class, chatId1);
        System.out.println(chatLinkCount1);
        assertEquals(chatLinkCount1, 0L);
        Long chatLinkCount2 =
            jdbcTemplate.queryForObject("select count(*) from tg_chat_links where tg_chat_id = ?", Long.class, chatId2);
        assertEquals(chatLinkCount2, 1L);
        System.out.println(chatLinkCount2);

        Long linkCount = jdbcTemplate.queryForObject(
            "select count(*) from links where uri = ?",
            Long.class,
            "https://stackoverflow.com/"
        );
        assertEquals(linkCount, 0L);
    }

    @Test
    @Transactional
    @Rollback
    void testFindAllHaveLink() {
        initChats();
        initLink();
        List<Link> links = linkRepository.findAll();
        assertThat(links.size()).isEqualTo(2);
        List<String> uris = links.stream().map(link -> link.getUri().toString()).toList();
        assertThat(uris).contains("https://stackoverflow.com/");
    }

    @Test
    @Transactional
    @Rollback
    void testFindAllNoHaveLink() {
        List<Link> links = linkRepository.findAll();
        assertThat(links.size()).isEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback
    void testFindByChatIdHaveLinks() {
        initChats();
        initLink();
        Long chatId2 = jdbcTemplate.queryForObject("select id from tg_chats where chat_id = ?", Long.class, 153L);
        List<Link> links = linkRepository.findByChatId(chatId2);
        assertThat(links.size()).isEqualTo(2);
        List<String> uris = links.stream().map(link -> link.getUri().toString()).toList();
        assertThat(uris).contains("https://stackoverflow.com/");
        assertThat(uris).contains("https://github.com/anekoss/tinkoff-project");
    }

    @Test
    @Transactional
    @Rollback
    void testFindByChatIdWithoutLinks() {
        initChats();
        Long chatId1 = jdbcTemplate.queryForObject("select id from tg_chats where chat_id = ?", Long.class, 210L);
        List<Link> links = linkRepository.findByChatId(chatId1);
        assertThat(links.size()).isEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback
    void testFindIdByUriExistLink() {
        initChats();
        initLink();
        Optional<Long> id = linkRepository.findIdByUri(URI.create("https://stackoverflow.com/"));
        assertThat(id).isPresent();
        assertThat(id.get()).isGreaterThan(0L);
    }

    @Test
    @Transactional
    @Rollback
    void testFindIdByUriNoExistLink() {
        Optional<Long> id = linkRepository.findIdByUri(URI.create("https://stackoverflow.com/"));
        assertThat(id).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void testFindStaleLinks() {
        jdbcTemplate.update(
            "insert into links(uri, type, updated_at, checked_at) values(?, ?, ?, ?)",
            "https://github.com/anekoss/tinkoff-project",
            GITHUB.toString(),
            OffsetDateTime.now(),
            OffsetDateTime.MAX
        );
        jdbcTemplate.update(
            "insert into links(uri, type, updated_at, checked_at) values(?, ?, ?, ?)",
            "https://stackoverflow.com/",
            STACKOVERFLOW.toString(),
            OffsetDateTime.now(),
            OffsetDateTime.MIN
        );
        List<Link> links = linkRepository.findStaleLinks(1L);
        assertThat(links.size()).isEqualTo(1);
        assertThat(links.getFirst().getType()).isEqualTo(STACKOVERFLOW);
    }

    @Test
    @Transactional
    @Rollback
    void testFindStaleLinksWithoutLinks() {
        List<Link> links = linkRepository.findStaleLinks(1L);
        assertThat(links.size()).isEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback
    void updateTestNotExistLinks() {
        assertThat(linkRepository.update(1L, OffsetDateTime.now(), OffsetDateTime.now())).isEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback
    void updateTestExistLinks() {
        String uri = "https://github.com/anekoss/tinkoff-project";
        jdbcTemplate.update(
            "insert into links(uri, type, updated_at, checked_at) values(?, ?, ?, ?)",
            uri,
            GITHUB.toString(),
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
        Long githubLinkId = jdbcTemplate.queryForObject(
            "select id from links where uri = ?",
            Long.class,
            uri
        );
        OffsetDateTime updated = OffsetDateTime.of(200, 10, 25, 20, 21, 7, 0, ZoneOffset.UTC);
        assertThat(linkRepository.update(
            githubLinkId,
            updated,
            OffsetDateTime.now()
        )).isEqualTo(1);
        List<Link> links = linkRepository.findAll();
        assertThat(links.size()).isEqualTo(1);
    }

}
