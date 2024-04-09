package edu.java.scrapper.repository.jpa;

import edu.java.domain.LinkEntity;
import edu.java.domain.LinkType;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.IntegrationTest;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import static edu.java.domain.LinkType.STACKOVERFLOW;
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JpaLinkRepositoryTest extends IntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JpaLinkRepository linkRepository;

    @Test
    @Rollback
    @Transactional
    void testFindByUri_shouldCorrectlyReturnLinkIfExist() {
        URI uri = URI.create("https://github.com/anekoss/tinkoff");
        Optional<LinkEntity> link = linkRepository.findByUri(uri);
        assert link.isPresent();
        assert link.get().getLinkType() == LinkType.GITHUB;
        assert link.get().getTgChats().size() == 3;
    }

    @Test
    @Rollback
    @Transactional
    void testFindByUri_shouldReturnEmptyOptionalIfNoExist() {
        URI uri = URI.create("https://github.com/anekoss/tinkoff34325");
        Optional<LinkEntity> link = linkRepository.findByUri(uri);
        assert link.isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void testFindStaleLinks_shouldCorrectlyReturnLinks() {
        OffsetDateTime checkedAt = OffsetDateTime.of(1900, 12, 12, 12, 12, 12, 12, UTC);
        jdbcTemplate.update(
            "insert into links(uri, link_type, updated_at, checked_at) values(?, ?, ?, ?)",
            "https://stackoverflow.com/",
            STACKOVERFLOW.toString(),
            Timestamp.from(OffsetDateTime.now().toInstant()),
            Timestamp.from(checkedAt.toInstant())
        );
        List<LinkEntity> links = linkRepository.findStaleLinks(1L);
        assert links.size() == 1;
        assertEquals(links.getFirst().getUri().toString(), "https://stackoverflow.com/");
    }

    @Test
    @Transactional
    @Rollback
    void testFindStaleLinks_shouldThrowExceptionIfNoList() {
        jdbcTemplate.update("delete from links");
        List<LinkEntity> links = linkRepository.findStaleLinks(1L);
        assert links.isEmpty();
    }

}
