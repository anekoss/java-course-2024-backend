package edu.java.repository.jdbc;

import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.repository.LinkRepository;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JdbcLinkRepository implements LinkRepository {
    private static final String FIELD_URI = "uri";
    private static final String FIELD_UPDATED_AT = "updated_at";
    private static final String FIELD_CHECKED_AT = "checked_at";
    private static final String FIELD_TYPE = "link_type";
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public int save(Long tgChatId, Link link) {
        Optional<Long> linkId = findIdByUri(link.getUri());
        if (linkId.isEmpty()) {
            jdbcTemplate.update(
                "insert into links (uri, type, updated_at, checked_at) values (?, ?, ?, ?)",
                link.getUri().toString(),
                link.getLinkType().toString(),
                link.getUpdatedAt(),
                link.getCheckedAt()
            );
            linkId = findIdByUri(link.getUri());
            if (link.getLinkType() == LinkType.STACKOVERFLOW) {
                jdbcTemplate.update("insert into stackOverflow_links(link_id, answer_count) values (?, ?)", linkId, 0L);
            }
        }
        return jdbcTemplate.update(
            "insert into tg_chat_links(tg_chat_id, link_id) values (?, ?)",
            tgChatId,
            linkId.get()
        );
    }

    @Override
    public Long findStackOverflowAnswerCountByLinkId(Long id) {
        return jdbcTemplate.queryForObject(
            "select answer_count from stackOverflow_links where link_id = ?",
            new BeanPropertyRowMapper<>(
                Long.class),
            id
        );
    }

    @Override
    @Transactional
    public int delete(Long tgChatId, URI uri) {
        Optional<Long> linkId = findIdByUri(uri);
        if (linkId.isPresent()) {
            String countLinksQuery = "select count(*) from tg_chat_links where link_id = ?";
            Long countLinks = jdbcTemplate.queryForObject(countLinksQuery, Long.class, linkId.get());
            if (countLinks == 1L) {
                return jdbcTemplate.update("delete from links where id = ?", linkId.get());
            }
            String deleteQuery = "delete from tg_chat_links where tg_chat_id = ? and link_id = ?";
            return jdbcTemplate.update(deleteQuery, tgChatId, linkId.get());
        }
        return 0;
    }

    @Override
    @Transactional
    public List<Link> findAll() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from links");
        List<Link> links = new ArrayList<>();
        list.forEach(m -> {
            Link link = new Link(URI.create((String) m.get(FIELD_URI)), LinkType.valueOf((String) m.get(FIELD_TYPE)));
            link.setUpdatedAt(((Timestamp) m.get(FIELD_UPDATED_AT)).toInstant().atOffset(ZoneOffset.UTC));
            link.setCheckedAt(((Timestamp) m.get(FIELD_UPDATED_AT)).toInstant().atOffset(ZoneOffset.UTC));
            link.setId((Long) m.get("id"));
            links.add(link);
        });
        return links;
    }

    @Override
    public List<Link> findByChatId(Long tgChatId) {
        String query =
            "select * from tg_chat_links join links on tg_chat_links.link_id = links.id "
                + "where tg_chat_links.tg_chat_id = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query, tgChatId);
        List<Link> links = new ArrayList<>();
        list.forEach(m -> {
            Link link = new Link(URI.create((String) m.get(FIELD_URI)), LinkType.valueOf((String) m.get(FIELD_TYPE)));
            link.setUpdatedAt(((Timestamp) m.get(FIELD_UPDATED_AT)).toInstant().atOffset(ZoneOffset.UTC));
            link.setCheckedAt(((Timestamp) m.get(FIELD_CHECKED_AT)).toInstant().atOffset(ZoneOffset.UTC));
            link.setId((Long) m.get("id"));
            links.add(link);
        });
        return links;
    }

    public Optional<Long> findIdByUri(URI uri) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select id from links where uri = ?", Long.class,
                uri.toString()
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Link> findStaleLinks(Long limit) {
        List<Map<String, Object>> list =
            jdbcTemplate.queryForList("select * from links order by checked_at asc limit ?", limit);
        List<Link> links = new ArrayList<>();
        list.forEach(m -> {
            Link link = new Link(URI.create((String) m.get(FIELD_URI)), LinkType.valueOf((String) m.get(FIELD_TYPE)));
            link.setUpdatedAt(((Timestamp) m.get(FIELD_UPDATED_AT)).toInstant().atOffset(ZoneOffset.UTC));
            link.setCheckedAt(((Timestamp) m.get(FIELD_CHECKED_AT)).toInstant().atOffset(ZoneOffset.UTC));
            link.setId((Long) m.get("id"));
            links.add(link);
        });
        return links;
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

    @Override
    public int updateAnswerCountByLinkId(Long answerCount, Long linkId) {
        return jdbcTemplate.update(
            "update stackOverflow_links set answer_count = ? where link_id = ?",
            answerCount, linkId
        );
    }
}
