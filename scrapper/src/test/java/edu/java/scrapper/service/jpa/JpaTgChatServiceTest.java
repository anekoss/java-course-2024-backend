package edu.java.scrapper.service.jpa;

import edu.java.ScrapperApplication;
import edu.java.scrapper.service.service.TgChatServiceTest;
import edu.java.service.jpa.JpaTgChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(classes = ScrapperApplication.class, properties = {"app.db-access=jpa"})
public class JpaTgChatServiceTest extends TgChatServiceTest {
    public JpaTgChatServiceTest(@Autowired JpaTgChatService tgChatService, @Autowired JdbcTemplate jdbcTemplate) {
        super(tgChatService, jdbcTemplate);
    }
}
