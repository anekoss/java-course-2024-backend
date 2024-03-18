package edu.java.repository.jdbc;

import edu.java.domain.Link;
import edu.java.repository.LinkRepository;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JdbcLinkRepository implements LinkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public int save(Long chatId, Link link) {
        Long linkId = jdbcTemplate.queryForObject("select * from links where uri = ?", Long.class, link.getUri());
        if (linkId == null) {
            jdbcTemplate.update(
                "insert into links(uri, type, updated_at, checked_at)",
                link.getUri(),
                link.getType(),
                link.getUpdatedAt(),
                link.getCheckedAt()
            );
            linkId = jdbcTemplate.queryForObject("select * from links where uri = ?", Long.class, link.getUri());
        }
        Long tgChatId = jdbcTemplate.queryForObject("select * from chats where chat_id = ?", Long.class, chatId);
        return jdbcTemplate.update("insert into chat_links(chat_id, link_id) values (?, ?)", tgChatId, linkId);
    }

    @Override
    public int delete(Long chatId, URI uri) {
        Long tgChatId = jdbcTemplate.queryForObject("select * from chats where chat_id = ?", Long.class, chatId);
        Long linkId = jdbcTemplate.queryForObject("select * from links where uri = ?", Long.class, uri);
        Long countLinks =
            jdbcTemplate.queryForObject("select count(*) from chat_links where link_id = ?", Long.class, linkId);
        if (countLinks == 1) {
            return jdbcTemplate.update("delete from links where linkId = ?", linkId);
        }
        return jdbcTemplate.update("delete from chat_links where chat_id = ? and link_id = ?", chatId, linkId);
    }

    @Override
    public List<Link> findAll() {
        return jdbcTemplate.queryForList("select * from links", Link.class);
    }

    @Override
    public List<Link> findByChatId(Long chatId) {
        return jdbcTemplate.queryForList(
            "select * from chat_links join links on chat_links.link_id = links.id where chat_links.chat_id = ?",
            Link.class,
            chatId
        );
    }
}
