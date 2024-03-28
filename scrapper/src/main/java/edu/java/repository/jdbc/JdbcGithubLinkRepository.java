package edu.java.repository.jdbc;

import edu.java.repository.GithubLinkRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JdbcGithubLinkRepository implements GithubLinkRepository {
    private final JdbcTemplate jdbcTemplate;

    public Optional<Long> findGithubBranchCountByLinkId(Long linkId) {
        try {
            Long count = jdbcTemplate.queryForObject(
                "select branch_count from github_links where link_id = ?",
                Long.class,
                linkId
            );
            return Optional.of(count);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int add(Long linkId, Long branchCount) {
        Long count = jdbcTemplate.queryForObject(
            "select count(*) from github_links where link_id = ?",
            Long.class,
            linkId
        );
        if (count != 0L) {
            return 0;
        }
        return jdbcTemplate.update(
            "insert into github_links (link_id, branch_count) values (?, ?)",
            linkId,
            branchCount
        );
    }

    public int update(Long linkId, Long count) {
        return jdbcTemplate.update("update github_links set branch_count = ? where link_id = ?", count, linkId);
    }
}

