package edu.java.repository.jdbc;

import edu.java.repository.StackOverflowLinkRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JdbcStackOverflowLinkRepository implements StackOverflowLinkRepository {
    private final JdbcTemplate jdbcTemplate;

    public Optional<Long> findStackOverflowAnswerCountByLinkId(Long linkId) {
        try {
            Long count = jdbcTemplate.queryForObject(
                "select answer_count from stackoverflow_links where link_id = ?",
                Long.class,
                linkId
            );
            return Optional.of(count);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int add(Long linkId, Long answer_count) {
        Long count = jdbcTemplate.queryForObject(
            "select count(*) from stackoverflow_links where link_id = ?",
            Long.class,
            linkId
        );
        if (count != 0L) {
            return 0;
        }
        return jdbcTemplate.update(
            "insert into stackoverflow_links(link_id, answer_count) values (?, ?)",
            linkId,
            answer_count
        );
    }

    public int update(Long linkId, Long count) {
        return jdbcTemplate.update("update stackoverflow_links set answer_count = ? where link_id = ?", count, linkId);
    }
}
