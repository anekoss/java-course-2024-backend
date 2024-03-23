package edu.java.repository.jdbc;

import edu.java.domain.TgChat;
import edu.java.repository.TgChatRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JdbcTgChatRepository implements TgChatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public int save(TgChat tgChat) {
        return jdbcTemplate.update("insert into tg_chats(chat_id) values (?)", tgChat.getChatId());
    }

    @Override
    @Transactional
    public int delete(TgChat tgChat) {
        String query =
            "select link_id from tg_chat_links where link_id in (select link_id from tg_chat_links "
                + "join tg_chats on tg_chat_links.tg_chat_id = tg_chats.id where tg_chats.chat_id = ?) "
                + "group by link_id having count(*)=1";
        try {
            List<Long> linkIds = jdbcTemplate.queryForList(query, Long.class, tgChat.getId());
            for (Long id : linkIds) {
                jdbcTemplate.update("delete from links where id = ?", id);
            }
        } catch (EmptyResultDataAccessException e) {
        }
        return jdbcTemplate.update("delete from tg_chats where id = ?", tgChat.getId());
    }

    @Override
    @Transactional
    public List<TgChat> findAll() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from tg_chats join ");
        List<TgChat> chats = new ArrayList<>();
        list.forEach(m -> {
            TgChat chat = new TgChat((Long) m.get("chat_id"));
            chat.setId((Long) m.get("id"));
            chats.add(chat);
        });
        return chats;
    }

    @Override
    @Transactional
    public Optional<TgChat> findByChatId(Long chatId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                "select * from tg_chats where chat_id = ?",
                new BeanPropertyRowMapper<>(TgChat.class),
                chatId
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }



    @Override
    public List<Long> findChatIdsByLinkId(Long linkId) {
        return jdbcTemplate.queryForList(
            "select tg_chats.chat_id from tg_chat_links join tg_chats on tg_chat_links.tg_chat_id = tg_chats.id"
                + " where tg_chat_links.link_id = ?",
            Long.class,
            linkId
        );
    }
}
