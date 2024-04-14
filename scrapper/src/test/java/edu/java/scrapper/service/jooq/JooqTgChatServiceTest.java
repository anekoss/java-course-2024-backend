package edu.java.scrapper.service.jooq;

import edu.java.ScrapperApplication;
import edu.java.scrapper.service.service.TgChatServiceTest;
import edu.java.service.jooq.JooqTgChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(classes = ScrapperApplication.class, properties = {"app.db-access=jooq"})
public class JooqTgChatServiceTest extends TgChatServiceTest {
    public JooqTgChatServiceTest(@Autowired JooqTgChatService tgChatService, @Autowired JdbcTemplate jdbcTemplate) {
        super(tgChatService, jdbcTemplate);
    }
}
