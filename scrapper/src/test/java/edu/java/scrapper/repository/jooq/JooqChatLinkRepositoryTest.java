package edu.java.scrapper.repository.jooq;

import edu.java.repository.jooq.JooqChatLinkRepository;
import edu.java.scrapper.repository.repository.ChatLinkRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class JooqChatLinkRepositoryTest extends ChatLinkRepositoryTest {

    public JooqChatLinkRepositoryTest(
        @Autowired JooqChatLinkRepository chatLinkRepository,
        @Autowired JdbcTemplate jdbcTemplate
    ) {
        super(chatLinkRepository, jdbcTemplate);
    }
}
