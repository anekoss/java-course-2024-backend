package edu.java.scrapper.repository.jdbc;

import edu.java.repository.jdbc.JdbcStackOverflowLinkRepository;
import edu.java.scrapper.IntegrationTest;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JdbcStackOverflowLinkRepositoryTest extends IntegrationTest {
    @Autowired
    private JdbcStackOverflowLinkRepository stackOverflowLinkRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Rollback
    @Transactional
    void testAdd() {
        assertThat(stackOverflowLinkRepository.add(3L, 2L)).isEqualTo(1);
        Long count = jdbcTemplate.queryForObject("select answer_count from stackoverflow_links where link_id = ?", Long.class, 3L);
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @Rollback
    @Transactional
    void testAddExist() {
        assertThat(stackOverflowLinkRepository.add(2L, 5L)).isEqualTo(0);
        Long count = jdbcTemplate.queryForObject("select answer_count from stackoverflow_links where link_id = ?", Long.class, 2L);
        assertThat(count).isEqualTo(3L);
    }

    @Test
    @Rollback
    @Transactional
    void testFindGithubBranchCountByLinkId() {
        Optional<Long> count = stackOverflowLinkRepository.findStackOverflowAnswerCountByLinkId(2L);
        assertThat(count).isPresent();
        assertThat(count.get()).isEqualTo(3L);
    }

    @Test
    @Rollback
    @Transactional
    void testFindGithubBranchCountByLinkIdNotFound() {
        Optional<Long> count = stackOverflowLinkRepository.findStackOverflowAnswerCountByLinkId(5L);
        assertThat(count).isEmpty();
    }

    @Test
    @Rollback
    @Transactional
    void testUpdate() {
        assertThat(stackOverflowLinkRepository.update(2L, 444L)).isEqualTo(1);
        Long count = jdbcTemplate.queryForObject("select answer_count from stackoverflow_links where link_id = ?", Long.class, 2L);
        assertThat(count).isEqualTo(444L);
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateNotFound() {
        assertThat(stackOverflowLinkRepository.update(4L, 444L)). isEqualTo(0);
    }

}

