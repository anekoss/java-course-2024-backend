package edu.java.repository.jdbc;

import edu.java.domain.TgChat;
import edu.java.repository.TgChatRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JdbcTgChatRepository implements TgChatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public int save(TgChat tgChat) {
        return jdbcTemplate.update("insert into tg_chats(chat_id) values (?)", tgChat.getChatId());
    }

    @Override
    public int delete(TgChat tgChat) {
        String query =
            "select link_id from tg_chat_links where link_id in (select link_id from tg_chat_links"
                + "join tg_chats on tg_chat_links.tg_chat_id = tg_chats.id where tg_chats.chat_id = ?)"
                + "group by link_id having count(*)==1";
        List<Long> links = jdbcTemplate.queryForList(query, Long.class, tgChat.getId());
        if (links != null && !links.isEmpty()) {
            jdbcTemplate.update("delete from links where id in ?", links);
        }
        return jdbcTemplate.update("delete from tg_chat_links where tg_chat_id = ?", tgChat.getId());
    }

    @Override
    public List<TgChat> findAll() {
        return jdbcTemplate.queryForList("select * from tg_chats", TgChat.class);
    }

    @Override
    public Optional<TgChat> findByChatId(Long chatId) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(
            "select * from tg_chats where chat_id = ?",
            TgChat.class,
            chatId
        ));
    }

    @Override
    public List<Long> findChatIdsByLinkId(Long linkId) {
        return jdbcTemplate.queryForList(
            "select chats.id from chat_links join chats on chat_links.chat_id chats.id where chat_links.link_id = ?",
            Long.class,
            linkId
        );
    }
}
