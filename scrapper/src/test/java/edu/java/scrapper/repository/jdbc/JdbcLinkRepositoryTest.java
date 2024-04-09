package edu.java.scrapper.repository.jdbc;

import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.repository.repository.LinkRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class JdbcLinkRepositoryTest extends LinkRepositoryTest {

    public JdbcLinkRepositoryTest(@Autowired JdbcLinkRepository linkRepository, @Autowired JdbcTemplate jdbcTemplate) {
        super(linkRepository, jdbcTemplate);
    }
}
