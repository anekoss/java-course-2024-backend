package edu.java.scrapper.repository.jdbc;

import edu.java.repository.jdbc.JdbcTgChatRepository;
import edu.java.scrapper.repository.repository.TgChatRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class JdbcTgChatRepositoryTest extends TgChatRepositoryTest {

    public JdbcTgChatRepositoryTest(
        @Autowired JdbcTgChatRepository tgChatRepository,
        @Autowired JdbcTemplate jdbcTemplate
    ) {
        super(tgChatRepository, jdbcTemplate);
    }

}
