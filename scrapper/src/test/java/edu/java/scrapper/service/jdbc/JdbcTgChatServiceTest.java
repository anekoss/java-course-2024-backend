package edu.java.scrapper.service.jdbc;

import edu.java.ScrapperApplication;
import edu.java.scrapper.service.service.TgChatServiceTest;
import edu.java.service.jdbc.JdbcTgChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(classes = ScrapperApplication.class, properties = {"app.db-access=jdbc"})
public class JdbcTgChatServiceTest extends TgChatServiceTest {
    public JdbcTgChatServiceTest(@Autowired JdbcTgChatService tgChatService, @Autowired JdbcTemplate jdbcTemplate) {
        super(tgChatService, jdbcTemplate);
    }
}
