package edu.java.repository.jdbc;

import edu.java.controller.exception.ChatAlreadyExistException;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.domain.TgChatEntity;
import edu.java.repository.TgChatRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import static edu.java.repository.jdbc.JdbcMapper.listMapToTgChatList;

@Repository
@AllArgsConstructor
public class JdbcTgChatRepository implements TgChatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public long add(@NotNull TgChatEntity tgChat) throws ChatAlreadyExistException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "insert into tg_chats (chat_id) values (?) returning id",
                    Statement.RETURN_GENERATED_KEYS
                );
                ps.setLong(1, tgChat.getChatId());
                return ps;
            }, keyHolder);
            return keyHolder.getKey().longValue();
        } catch (DataAccessException e) {
            throw new ChatAlreadyExistException();
        }
    }

    @Override
    @Transactional
    public long remove(@NotNull TgChatEntity tgChat) throws ChatNotFoundException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int update = jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                connection.prepareStatement(
                    "delete from tg_chats where chat_id = ? returning id",
                    Statement.RETURN_GENERATED_KEYS
                );
            ps.setLong(1, tgChat.getChatId());
            return ps;
        }, keyHolder);
        if (update == 0 || keyHolder.getKey() == null) {
            throw new ChatNotFoundException();
        }
        return keyHolder.getKey().longValue();
    }

    @Override
    @Transactional
    public List<TgChatEntity> findAll() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from tg_chats");
        return listMapToTgChatList(list);
    }

    @Override
    @Transactional
    public TgChatEntity findByChatId(Long chatId) throws ChatNotFoundException {
        try {
            Long id = jdbcTemplate.queryForObject("select id from tg_chats where chat_id = ?", Long.class, chatId);
            return new TgChatEntity().setId(id).setChatId(chatId);
        } catch (DataAccessException e) {
            throw new ChatNotFoundException();
        }
    }

    @Override
    @Transactional
    public Optional<TgChatEntity> findById(Long id) {
        try {
            Long chatId = jdbcTemplate.queryForObject("select chat_id from tg_chats where id = ?", Long.class, id);
            return Optional.of(new TgChatEntity().setId(id).setChatId(chatId));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

}
