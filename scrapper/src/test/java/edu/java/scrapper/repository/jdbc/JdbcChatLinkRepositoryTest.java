package edu.java.scrapper.repository.jdbc;

import edu.java.repository.jdbc.JdbcChatLinkRepository;
import edu.java.scrapper.repository.repository.ChatLinkRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class JdbcChatLinkRepositoryTest extends ChatLinkRepositoryTest {

    public JdbcChatLinkRepositoryTest(
        @Autowired JdbcChatLinkRepository chatLinkRepository,
        @Autowired JdbcTemplate jdbcTemplate
    ) {
        super(chatLinkRepository, jdbcTemplate);
    }
}
