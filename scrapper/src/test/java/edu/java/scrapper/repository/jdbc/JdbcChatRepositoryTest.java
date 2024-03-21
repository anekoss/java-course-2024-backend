package edu.java.scrapper.repository.jdbc;

import edu.java.domain.TgChat;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.JdbcTgChatRepository;
import edu.java.scrapper.IntegrationTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JdbcChatRepositoryTest extends IntegrationTest {
    @Autowired
    private JdbcLinkRepository linkRepository;
    @Autowired
    private JdbcTgChatRepository tgChatRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    @Rollback
    void addTest() {
        TgChat tgChat = new TgChat(214L);
        assertThat(tgChatRepository.save(tgChat)).isEqualTo(1);
        TgChat actual = jdbcTemplate.queryForObject("select * from tg_chats where chat_id = ?", TgChat.class, 214L);
        assertThat(actual).isEqualTo(tgChat);
    }

    @Test
    @Transactional
    @Rollback
    void removeTest() {
    }
}
