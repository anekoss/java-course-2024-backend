package edu.java.scrapper.repository.jooq;

import edu.java.repository.jooq.JooqLinkRepository;
import edu.java.scrapper.repository.repository.LinkRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class JooqLinkRepositoryTest extends LinkRepositoryTest {

    public JooqLinkRepositoryTest(@Autowired JooqLinkRepository linkRepository, @Autowired JdbcTemplate jdbcTemplate) {
        super(linkRepository, jdbcTemplate);
    }
}
