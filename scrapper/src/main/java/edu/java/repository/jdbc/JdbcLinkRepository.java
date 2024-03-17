package edu.java.repository.jdbc;

import edu.java.domain.Link;
import edu.java.repository.LinkRepository;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcLinkRepository implements LinkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public int save(Link link) {
        return jdbcTemplate.update("insert into links(uri, type) values (?, ?)", link.getUri(), link.getType());
    }

    @Override
    public int delete(Link link) {
        return jdbcTemplate.update("delete from links where id = ?", link.getId());
    }

    @Override
    public Link findByUri(URI uri) {
        try {
            return jdbcTemplate.queryForObject("select * from links where uri = ?", Link.class, uri);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Link> findAll() {
        try {
            return jdbcTemplate.queryForList("select * from links", Link.class);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    public List<Link> findStaleLinks(long limit) {
        try {
            return jdbcTemplate.queryForList("select * from links order by checked_at desc limit ?", Link.class, limit);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }
}
