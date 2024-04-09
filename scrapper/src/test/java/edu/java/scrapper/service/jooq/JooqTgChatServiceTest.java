package edu.java.scrapper.service.jooq;

import edu.java.scrapper.service.service.TgChatServiceTest;
import edu.java.service.jdbc.JdbcTgChatService;
import edu.java.service.jooq.JooqTgChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class JooqTgChatServiceTest extends TgChatServiceTest {
    public JooqTgChatServiceTest(@Autowired JooqTgChatService tgChatService, @Autowired JdbcTemplate jdbcTemplate) {
        super(tgChatService, jdbcTemplate);
    }
}
