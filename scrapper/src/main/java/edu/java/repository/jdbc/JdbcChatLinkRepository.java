package edu.java.repository.jdbc;

import edu.java.controller.exception.LinkAlreadyExistException;
import edu.java.controller.exception.LinkNotFoundException;
import edu.java.domain.ChatLink;
import edu.java.repository.ChatLinkRepository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import static edu.java.repository.jdbc.JdbcMapper.listMapToChatLinkList;

@Repository
@RequiredArgsConstructor
public class JdbcChatLinkRepository implements ChatLinkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public long add(ChatLink chatLink) throws LinkAlreadyExistException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "insert into tg_chat_links(tg_chat_id, link_id) values (?, ?) returning id",
                    Statement.RETURN_GENERATED_KEYS
                );
                ps.setLong(1, chatLink.tgChatId());
                ps.setLong(2, chatLink.linkId());
                return ps;
            }, keyHolder);
            return keyHolder.getKey().longValue();
        } catch (DataAccessException e) {
            throw new LinkAlreadyExistException();
        }
    }

    @Override
    public long remove(ChatLink chatLink) throws LinkNotFoundException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int update = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "delete from tg_chat_links where tg_chat_id = ? and link_id = ? returning id",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, chatLink.tgChatId());
            ps.setLong(2, chatLink.linkId());
            return ps;
        }, keyHolder);
        if (update == 0) {
            throw new LinkNotFoundException();
        }
        return keyHolder.getKey().longValue();
    }

    @Override
    public List<ChatLink> findByTgChatId(long tgChatId) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
            "select * from tg_chat_links where tg_chat_id = ?", tgChatId);
        return listMapToChatLinkList(list);

    }

    @Override
    public List<ChatLink> findByLinkId(long linkId) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
            "select * from tg_chat_links where link_id = ?", linkId);
        return listMapToChatLinkList(list);
    }

}
