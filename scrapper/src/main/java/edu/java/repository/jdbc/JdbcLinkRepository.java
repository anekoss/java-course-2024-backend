package edu.java.repository.jdbc;

import edu.java.domain.Link;
import edu.java.repository.LinkRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JdbcLinkRepository implements LinkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public int save(Long tgChatId, Link link) {
        jdbcTemplate.update(
            "insert into links(uri, type, updated_at, checked_at)",
            link.getUri(),
            link.getType(),
            link.getUpdatedAt(),
            link.getCheckedAt()
        );
        Long linkId = findIdByUri(link.getUri());
        return jdbcTemplate.update("insert into tg_chat_links(tg_chat_id, link_id) values (?, ?)", tgChatId, linkId);
    }

    @Override
    public int delete(Long tgChatId, URI uri) {
        Long linkId = findIdByUri(uri);
        String countLinksQuery = "select count(*) from tg_chat_links where link_id = ?";
        Long countLinks = jdbcTemplate.queryForObject(countLinksQuery, Long.class, linkId);
        if (countLinks == 1L) {
            return jdbcTemplate.update("delete from links where linkId = ?", linkId);
        }
        String deleteQuery = "delete from tg_chat_links where tg_chat_id = ? and link_id = ?";
        return jdbcTemplate.update(deleteQuery, tgChatId, linkId);
    }

    @Override
    public List<Link> findAll() {
        return jdbcTemplate.queryForList("select * from links", Link.class);
    }

    @Override
    public List<Link> findByChatId(Long tgChatId) {
        String query =
            "select * from tg_chat_links join links on tg_chat_links.link_id = links.id "
                + "where tg_chat_links.tg_chat_id = ?";
        return jdbcTemplate.queryForList(query, Link.class, tgChatId);
    }

    public Long findIdByUri(URI uri) {
        return jdbcTemplate.queryForObject("select id from links where uri = ?", Long.class, uri);
    }

    @Override
    public List<Link> findStaleLinks(Long limit) {
        return jdbcTemplate.queryForList("select * from links order by checked_at desc limit ?", Link.class, limit);
    }

    @Override
    public int update(Long linkId, OffsetDateTime updatedAt, OffsetDateTime checkedAt) {
        return jdbcTemplate.update(
            "update links set updated_at = ?, checked_at = ? where id = ?",
            updatedAt,
            checkedAt,
            linkId
        );
    }
}
