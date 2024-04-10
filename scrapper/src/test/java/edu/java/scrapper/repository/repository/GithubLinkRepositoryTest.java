package edu.java.scrapper.repository.repository;

import edu.java.domain.GithubLink;
import edu.java.repository.GithubLinkRepository;
import edu.java.scrapper.IntegrationTest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@AllArgsConstructor
public abstract class GithubLinkRepositoryTest extends IntegrationTest {
    private GithubLinkRepository githubLinkRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Rollback
    @Transactional
    void testAdd_shouldAddGithubLinkIfNotExistAndReturnBranchCount() {
        GithubLink githubLink = new GithubLink(3L, 5L);
        assert githubLinkRepository.add(githubLink) == 5L;
        Long actual = jdbcTemplate.queryForObject(
            "select branch_count from github_links where link_id = ?",
            Long.class,
            3L
        );
        assert actual != null;
        assert actual == 5L;
    }

    @Test
    @Rollback
    @Transactional
    void testAdd_shouldUpdateGithubLinkIfExistAndReturnOldBranchCount() {
        GithubLink githubLink = new GithubLink(1L, 5L);
        Long prevBranchCount = githubLinkRepository.add(githubLink);
        assertNotEquals(prevBranchCount, 5L);
        Long actual = jdbcTemplate.queryForObject(
            "select branch_count from github_links where link_id = ?",
            Long.class,
            1L
        );
        assert actual != null;
        assertEquals(actual, 5L);
    }
}
