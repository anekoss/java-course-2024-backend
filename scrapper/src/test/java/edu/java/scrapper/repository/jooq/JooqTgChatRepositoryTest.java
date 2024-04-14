package edu.java.scrapper.repository.jooq;

import edu.java.repository.jooq.JooqTgChatRepository;
import edu.java.scrapper.repository.repository.TgChatRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class JooqTgChatRepositoryTest extends TgChatRepositoryTest {

    public JooqTgChatRepositoryTest(
        @Autowired JooqTgChatRepository tgChatRepository,
        @Autowired JdbcTemplate jdbcTemplate
    ) {
        super(tgChatRepository, jdbcTemplate);
    }
}
