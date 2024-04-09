package edu.java.scrapper.repository.jdbc;

import edu.java.repository.jdbc.JdbcStackOverflowLinkRepository;
import edu.java.scrapper.repository.repository.StackOverflowLinkRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class JdbcStackOverflowLinkRepositoryTest extends StackOverflowLinkRepositoryTest {

    public JdbcStackOverflowLinkRepositoryTest(
        @Autowired JdbcStackOverflowLinkRepository stackOverflowLinkRepository,
        @Autowired JdbcTemplate jdbcTemplate
    ) {
        super(stackOverflowLinkRepository, jdbcTemplate);
    }
}

