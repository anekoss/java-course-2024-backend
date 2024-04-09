package edu.java.scrapper.repository.jooq;

import edu.java.repository.jooq.JooqGithubLinkRepository;
import edu.java.scrapper.repository.repository.GithubLinkRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class JooqGithubLinkRepositoryTest extends GithubLinkRepositoryTest {

    public JooqGithubLinkRepositoryTest(
        @Autowired JooqGithubLinkRepository githubLinkRepository,
        @Autowired JdbcTemplate jdbcTemplate
    ) {
        super(githubLinkRepository, jdbcTemplate);
    }
}

