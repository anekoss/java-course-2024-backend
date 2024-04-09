package edu.java.scrapper.repository.jdbc;

import edu.java.repository.jdbc.JdbcGithubLinkRepository;
import edu.java.scrapper.repository.repository.GithubLinkRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class JdbcGithubLinkRepositoryTest extends GithubLinkRepositoryTest {

    public JdbcGithubLinkRepositoryTest(@Autowired JdbcGithubLinkRepository githubLinkRepository, @Autowired JdbcTemplate jdbcTemplate) {
        super(githubLinkRepository, jdbcTemplate);
    }
}

