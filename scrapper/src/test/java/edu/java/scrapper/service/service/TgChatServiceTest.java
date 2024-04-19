package edu.java.scrapper.service.service;

import edu.java.controller.exception.ChatAlreadyExistException;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.domain.TgChatEntity;
import edu.java.scrapper.IntegrationTest;
import edu.java.service.TgChatService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AllArgsConstructor
public abstract class TgChatServiceTest extends IntegrationTest {

    private TgChatService tgChatService;

    private JdbcTemplate jdbcTemplate;

    @Test
    @Rollback
    @Transactional
    void testRegister_shouldCorrectlyRegisterNoExistChat() throws ChatAlreadyExistException {
        tgChatService.register(444L);
        TgChatEntity chat = jdbcTemplate.queryForObject(
            "select * from tg_chats where chat_id = ?",
            new BeanPropertyRowMapper<>(TgChatEntity.class),
            444L
        );
        assert chat != null;
        assert chat.getId() > 0L;
        assert chat.getChatId() == 444L;
    }

    @Test
    @Rollback
    @Transactional
    void testRegister_shouldThrowExceptionIfChatExist() {
        assertThrows(ChatAlreadyExistException.class, () -> tgChatService.register(555555L));
    }

    @Test
    @Rollback
    @Transactional
    void testUnregister_shouldCorrectlyUnregisterExistChat() throws ChatNotFoundException {
        tgChatService.unregister(555555L);
        assertThrows(
            DataAccessException.class,
            () -> jdbcTemplate.queryForObject("select * from tg_chats where chat_id = ?", TgChatEntity.class, 555555L)
        );
    }

    @Test
    @Rollback
    @Transactional
    void testUnregister_shouldThrowExceptionIfChatNoExist() {
        assertThrows(ChatNotFoundException.class, () -> tgChatService.unregister(999L));
    }

}
