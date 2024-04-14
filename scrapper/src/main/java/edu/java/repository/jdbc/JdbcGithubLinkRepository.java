package edu.java.repository.jdbc;

import edu.java.domain.GithubLink;
import edu.java.repository.GithubLinkRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JdbcGithubLinkRepository implements GithubLinkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public long add(GithubLink githubLink) {
        List<Long> oldBranchCount = jdbcTemplate.queryForList(
            "select branch_count from github_links where link_id = ?",
            Long.class,
            githubLink.linkId()
        );
        jdbcTemplate.update(
            "insert into github_links (link_id, branch_count) values (?, ?) on conflict (link_id) "
                + "do update set branch_count = EXCLUDED.branch_count",
            githubLink.linkId(),
            githubLink.branchCount()
        );
        return oldBranchCount.isEmpty() ? githubLink.branchCount() : oldBranchCount.getFirst();
    }

}

