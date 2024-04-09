package edu.java.scrapper.repository.jooq;

import edu.java.repository.jooq.JooqStackOverflowLinkRepository;
import edu.java.scrapper.repository.repository.StackOverflowLinkRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class JooqStackOverflowLinkRepositoryTest extends StackOverflowLinkRepositoryTest {

    public JooqStackOverflowLinkRepositoryTest(
        @Autowired JooqStackOverflowLinkRepository stackOverflowLinkRepository,
        @Autowired JdbcTemplate jdbcTemplate
    ) {
        super(stackOverflowLinkRepository, jdbcTemplate);
    }
}

