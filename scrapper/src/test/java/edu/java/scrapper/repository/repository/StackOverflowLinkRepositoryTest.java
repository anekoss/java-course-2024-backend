package edu.java.scrapper.repository.repository;

import edu.java.domain.StackOverflowLink;
import edu.java.repository.StackOverflowLinkRepository;
import edu.java.scrapper.IntegrationTest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;

@AllArgsConstructor
public abstract class StackOverflowLinkRepositoryTest extends IntegrationTest {
    private StackOverflowLinkRepository stackOverflowLinkRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Rollback
    @Transactional
    void testAdd_shouldAddStackOverflowIfNotExistAndReturnAnswerCount() {
        StackOverflowLink stackOverflowLink = new StackOverflowLink(1L, 5L);
        assert stackOverflowLinkRepository.add(stackOverflowLink) == 5L;
        Long actual = jdbcTemplate.queryForObject(
            "select answer_count from stackoverflow_links where link_id = ?",
            Long.class,
            1L
        );
        assert actual != null;
        assert actual == 5L;
    }

    @Test
    @Rollback
    @Transactional
    void testAdd_shouldUpdateStackOverflowLinkIfExistAndReturnOldAnswerCount() {
        StackOverflowLink stackOverflowLink = new StackOverflowLink(2L, 7L);
        assert stackOverflowLinkRepository.add(stackOverflowLink) == 3L;
        Long actual = jdbcTemplate.queryForObject(
            "select answer_count from stackoverflow_links where link_id = ?",
            Long.class,
            2L
        );
        assert actual != null;
        assert actual == 7L;
    }
}
