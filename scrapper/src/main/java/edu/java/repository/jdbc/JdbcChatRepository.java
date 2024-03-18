package edu.java.repository.jdbc;

import edu.java.domain.Chat;
import edu.java.repository.ChatRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JdbcChatRepository implements ChatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public int save(Chat chat) {
        return jdbcTemplate.update("insert into chats(chat_id) values (?)", chat.getChatId());
    }

    @Override
    public int delete(Chat chat) {
        List<Long> links = jdbcTemplate.queryForList("select link_id from chat_links where link_id in (select link_id from chat_links join chats on chat_links.chat_id = chat.id where chat.chat_id = ?) group by link_id having count(*)==1", Long.class, chat.getId());
        if (links != null && !links.isEmpty()) {
            jdbcTemplate.update("delete from links where id = ?", links);
        }
        return jdbcTemplate.update("delete from chat_links where chat_id = ?", chat.getId());
    }

    @Override
    public List<Chat> findAll() {
        return jdbcTemplate.queryForList("select * from chats", Chat.class);
    }

    @Override
    public Optional<Chat> findByChatId(Long chatId) {
        return Optional.ofNullable(jdbcTemplate.queryForObject("select * from chats where chat_id = ?", Chat.class, chatId));
    }
}
