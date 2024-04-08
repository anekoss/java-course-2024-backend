package edu.java.scrapper.repository.jdbc;

import edu.java.controller.exception.ChatAlreadyExistException;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.domain.TgChat;
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
        TgChat tgChat = new TgChat().setChatId(214L);
        long tgChatId = tgChatRepository.add(tgChat);
        assert tgChatId > 0;
        assertThat(tgChatId).isGreaterThan(0L);
        TgChat actual = jdbcTemplate.queryForObject("select * from tg_chats where chat_id = ?",
                new BeanPropertyRowMapper<>(TgChat.class), 214L
        );
        assert tgChatId == actual.getId();
        assert 214L == actual.getChatId();
    }

    @Test
    @Transactional
    @Rollback
    void testAdd_shouldThrowExceptionIfAddExistChat() {
        TgChat tgChat = new TgChat().setChatId(555555L);
        assertThrows(ChatAlreadyExistException.class, () -> tgChatRepository.add(tgChat));
    }

    @Test
    @Transactional
    @Rollback
    void testRemove_shouldCorrectlyRemoveExistChat() throws ChatNotFoundException {
        TgChat tgChat = new TgChat(4L, 555555L);
        assertThat(tgChatRepository.remove(tgChat)).isEqualTo(4L);
        assertThrows(
                EmptyResultDataAccessException.class,
                () -> jdbcTemplate.queryForObject("select * from tg_chats where chat_id = ?",
                        new BeanPropertyRowMapper<>(TgChat.class), 555555L
                )
        );
    }

    @Test
    @Transactional
    @Rollback
    void testRemove_shouldThrowExceptionIfRemoveNoExistChat() {
        TgChat tgChat = new TgChat(6L, 250L);
        assertThrows(ChatNotFoundException.class, () -> tgChatRepository.remove(tgChat));
    }

    @Transactional
    @Rollback
    @Test
    void testFindAll_shouldCorrectlyFindAllChats() {
        List<TgChat> chats = tgChatRepository.findAll();
        assert chats.size() == 4;
        assert chats.getFirst().getChatId() == 124025L;
        assert chats.getLast().getChatId() == 555555L;
    }

    @Test
    @Transactional
    @Rollback
    void testFindAll_shouldReturnEmptyListIfNoChats() {
        jdbcTemplate.update("delete from tg_chats");
        List<TgChat> chats = tgChatRepository.findAll();
        assert chats.isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void testFindByChatId_shouldCorrectlyFindChat() throws ChatNotFoundException {
        TgChat actual = tgChatRepository.findByChatId(444444L);
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
