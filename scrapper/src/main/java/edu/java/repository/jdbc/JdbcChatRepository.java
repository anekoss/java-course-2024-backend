package edu.java.repository.jdbc;

import edu.java.domain.Chat;
import edu.java.repository.ChatRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcChatRepository implements ChatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public int save(Chat chat) {
        return jdbcTemplate.update("insert into chats(chatId) values (?)", chat.getChatId());
    }

    @Override
    public int delete(Long id) {
        return jdbcTemplate.update("delete from chats where id = ?", id);
    }

    @Override
    public Chat findByChatId(Long chatId) {
        try {
            return jdbcTemplate.queryForObject("select * from chats where chatId = ?", Chat.class, chatId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Chat> findAll() {
        try {
            return jdbcTemplate.queryForList("select * from chats", Chat.class);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }
}
