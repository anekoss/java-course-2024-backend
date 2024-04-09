package edu.java.scrapper.repository.jdbc;

import edu.java.controller.exception.ChatAlreadyExistException;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.domain.TgChatEntity;
import edu.java.repository.jdbc.JdbcTgChatRepository;
import edu.java.scrapper.IntegrationTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class JdbcTgChatRepositoryTest extends IntegrationTest {
    @Autowired
    private JdbcTgChatRepository tgChatRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    @Rollback
    void testAdd_shouldCorrectlyAddNoExistChat() throws ChatAlreadyExistException {
        TgChatEntity tgChat = new TgChatEntity().setChatId(214L);
        long tgChatId = tgChatRepository.add(tgChat);
        assert tgChatId > 0;
        assertThat(tgChatId).isGreaterThan(0L);
        TgChatEntity actual = jdbcTemplate.queryForObject("select * from tg_chats where chat_id = ?",
                new BeanPropertyRowMapper<>(TgChatEntity.class), 214L
        );
        assert tgChatId == actual.getId();
        assert 214L == actual.getChatId();
    }

    @Test
    @Transactional
    @Rollback
    void testAdd_shouldThrowExceptionIfAddExistChat() {
        TgChatEntity tgChat = new TgChatEntity().setChatId(555555L);
        assertThrows(ChatAlreadyExistException.class, () -> tgChatRepository.add(tgChat));
    }

    @Test
    @Transactional
    @Rollback
    void testRemove_shouldCorrectlyRemoveExistChat() throws ChatNotFoundException {
        TgChatEntity tgChat = new TgChatEntity(4L, 555555L);
        assertThat(tgChatRepository.remove(tgChat)).isEqualTo(4L);
        assertThrows(
                EmptyResultDataAccessException.class,
                () -> jdbcTemplate.queryForObject("select * from tg_chats where chat_id = ?",
                        new BeanPropertyRowMapper<>(TgChatEntity.class), 555555L
                )
        );
    }

    @Test
    @Transactional
    @Rollback
    void testRemove_shouldThrowExceptionIfRemoveNoExistChat() {
        TgChatEntity tgChat = new TgChatEntity(6L, 250L);
        assertThrows(ChatNotFoundException.class, () -> tgChatRepository.remove(tgChat));
    }

    @Transactional
    @Rollback
    @Test
    void testFindAll_shouldCorrectlyFindAllChats() {
        List<TgChatEntity> chats = tgChatRepository.findAll();
        assert chats.size() == 4;
        assert chats.getFirst().getChatId() == 124025L;
        assert chats.getLast().getChatId() == 555555L;
    }

    @Test
    @Transactional
    @Rollback
    void testFindAll_shouldReturnEmptyListIfNoChats() {
        jdbcTemplate.update("delete from tg_chats");
        List<TgChatEntity> chats = tgChatRepository.findAll();
        assert chats.isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void testFindByChatId_shouldCorrectlyFindChat() throws ChatNotFoundException {
        TgChatEntity actual = tgChatRepository.findByChatId(444444L);
        assert actual.getId() == 3L;
        assert actual.getChatId() == 444444L;
    }

    @Test
    @Transactional
    @Rollback
    void testFindByChatId_shouldReturnEmptyOptionalIfNoChat() {
        ChatNotFoundException exception = assertThrows(ChatNotFoundException.class, () -> tgChatRepository.findByChatId(210L));
        assertEquals(exception.getMessage(), "Чат c таким id не найден");

    }

}
