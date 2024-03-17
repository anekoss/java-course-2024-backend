package edu.java.repository.jdbc;

import edu.java.domain.Link;
import edu.java.repository.ChatLinksRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcChatLinksRepository implements ChatLinksRepository {

    private final JdbcTemplate jdbcTemplate;

    public int save(Long linkId, Long chatId) {
        return jdbcTemplate.update("insert into chat_links(chat_id, link_id) values (?, ?)", linkId, chatId);
    }

    public int delete(Long linkId, Long chatId) {
        return jdbcTemplate.update("delete from chat_liinks where chat_id =? and link_id = ?", chatId, linkId);
    }

    public int deleteByChatId(Long chatId) {
        return jdbcTemplate.update("delete from chat_links where chat_id = ?", chatId);
    }


    public List<Link> findAllByChatId(Long chatId) {
        return jdbcTemplate.queryForList(
            "select * from chat_links join links on links.id = chat_links.link_id where chat_links.chat_id = ?",
            Link.class,
            chatId
        );
    }

    @Override
    public Link findByLinkIdAndChatId(Long linkId, Long chatId) {
        try {
            return jdbcTemplate.queryForObject(
                "select * from chat_links where link_id = ? and chat_id = ?",
                Link.class,
                linkId,
                chatId
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
