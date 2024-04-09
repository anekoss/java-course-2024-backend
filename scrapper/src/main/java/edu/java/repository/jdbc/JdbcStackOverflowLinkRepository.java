package edu.java.repository.jdbc;

import edu.java.domain.StackOverflowLink;
import edu.java.repository.StackOverflowLinkRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JdbcStackOverflowLinkRepository implements StackOverflowLinkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public long add(StackOverflowLink stackOverflowLink) {
        List<Long> oldAnswerCount = jdbcTemplate.queryForList(
            "select answer_count from stackoverflow_links where link_id = ?",
            Long.class,
            stackOverflowLink.linkId()
        );
        jdbcTemplate.update(
            "insert into stackoverflow_links (link_id, answer_count) values (?, ?) on conflict (link_id) "
                + "do update set answer_count = EXCLUDED.answer_count",
            stackOverflowLink.linkId(),
            stackOverflowLink.answerCount()
        );
        return oldAnswerCount.isEmpty() ? stackOverflowLink.answerCount() : oldAnswerCount.getFirst();
    }

}
