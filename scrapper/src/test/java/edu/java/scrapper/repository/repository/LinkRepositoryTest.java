package edu.java.scrapper.repository.repository;

import edu.java.controller.exception.LinkNotFoundException;
import edu.java.domain.LinkEntity;
import edu.java.repository.LinkRepository;
import edu.java.scrapper.IntegrationTest;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import static edu.java.domain.LinkType.GITHUB;
import static edu.java.domain.LinkType.STACKOVERFLOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AllArgsConstructor
public abstract class LinkRepositoryTest extends IntegrationTest {
    protected LinkRepository linkRepository;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    @Rollback
    void testAdd_shouldCorrectlyAddNoExistLink() {
        LinkEntity link = new LinkEntity().setUri(URI.create("https://stackoverflow.com/"))
                                          .setLinkType(STACKOVERFLOW)
                                          .setUpdatedAt(OffsetDateTime.now())
                                          .setCheckedAt(OffsetDateTime.now());
        long linkId = linkRepository.add(link);
        assert linkId > 0;
        LinkEntity actualLink = jdbcTemplate.queryForObject("select * from links where uri = ?",
            new BeanPropertyRowMapper<>(LinkEntity.class), "https://stackoverflow.com/"
        );
        assert actualLink != null;
        assert actualLink.getId() == linkId;
        assertEquals(actualLink.getUri(), link.getUri());
        assertEquals(actualLink.getLinkType(), link.getLinkType());
        assertThat(actualLink.getUpdatedAt()).isEqualToIgnoringNanos(link.getUpdatedAt());
        assertThat(actualLink.getCheckedAt()).isEqualToIgnoringNanos(link.getCheckedAt());
    }

    @Test
    @Transactional
    @Rollback
    void testAdd_shouldReturnIdExistLinkIfAddLinkExist() {
        LinkEntity link = new LinkEntity().setUri(URI.create("https://github.com/anekoss/tinkoff"))
                                          .setLinkType(GITHUB)
                                          .setUpdatedAt(OffsetDateTime.now())
                                          .setCheckedAt(OffsetDateTime.now());
        long linkId = linkRepository.add(link);
        assert linkId > 0;
        LinkEntity actualLink = jdbcTemplate.queryForObject("select * from links where id = ?",
            new BeanPropertyRowMapper<>(LinkEntity.class), linkId
        );
        assert actualLink != null;
        assertEquals(actualLink.getUri(), link.getUri());
    }

    @Test
    @Transactional
    @Rollback
    void testRemove_shouldThrowExceptionIfRemoveNoExistLink() {
        assertThrows(
            LinkNotFoundException.class,
            () -> linkRepository.remove(URI.create("https://stackoverflow.com/"))
        );
    }

    @Test
    @Transactional
    @Rollback
    void testRemove_shouldCorrectlyRemoveExistLink() throws LinkNotFoundException {
        long linkId = linkRepository.remove(URI.create("https://github.com/anekoss/tinkoff"));
        assert linkId > 0;
        Long linkCount = jdbcTemplate.queryForObject(
            "select count(*) from links where uri = ?",
            Long.class,
            "https://github.com/anekoss/tinkoff"
        );
        assert linkCount != null;
        assert linkCount == 0L;
    }

    @Test
    @Transactional
    @Rollback
    void testFindAll_shouldCorrectlyFindAllLink() {
        List<LinkEntity> links = linkRepository.findAll();
        assert links.size() == 3;
        assertThat(links.stream().map(link -> link.getUri().toString()).toList()).contains(
            "https://github.com/anekoss/tinkoff",
            "https://stackoverflow.com/questions/44760112/marching-cubes-generating-holes-in-mesh"
        );

    }

    @Test
    @Transactional
    @Rollback
    void testFindAll_shouldReturnEmptyListIfNoLinks() {
        jdbcTemplate.update("delete from links");
        List<LinkEntity> links = linkRepository.findAll();
        assert links.isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void testFindByUri_shouldCorrectlyFindExistLink() {
        URI uri = URI.create("https://stackoverflow.com/questions/44760112/marching-cubes-generating-holes-in-mesh");
        Optional<LinkEntity> link = linkRepository.findByUri(uri);
        assert link.isPresent();
        assert link.get().getId() > 0L;
        assertEquals(link.get().getUri(), uri);
    }

    @Test
    @Transactional
    @Rollback
    void testFindByUri_shouldReturnOptionalEmptyIfNoLink() {
        Optional<LinkEntity> id = linkRepository.findByUri(URI.create("https://stackoverflow.com/"));
        assertThat(id).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void testFindById_shouldCorrectlyFindExistLink() {
        URI uri = URI.create("https://github.com/anekoss/tinkoff");
        Optional<LinkEntity> link = linkRepository.findById(1L);
        assert link.isPresent();
        assert link.get().getId() == 1L;
        assertEquals(link.get().getUri(), uri);
    }

    @Test
    @Transactional
    @Rollback
    void testFindById_shouldReturnOptionalEmptyIfNoLink() {
        Optional<LinkEntity> id = linkRepository.findById(1139L);
        assertThat(id).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void testFindStaleLinks_shouldCorrectlyReturnLinks() {
        jdbcTemplate.update(
            "insert into links(uri, link_type, updated_at, checked_at) values(?, ?, ?, ?)",
            "https://stackoverflow.com/",
            STACKOVERFLOW.toString(),
            OffsetDateTime.now(),
            OffsetDateTime.MIN
        );
        List<LinkEntity> links = linkRepository.findStaleLinks(1L);
        assert links.size() == 1;
        assertEquals(links.getFirst().getUri().toString(), "https://stackoverflow.com/");
    }

    @Test
    @Transactional
    @Rollback
    void testFindStaleLinks_shouldReturnEmptyListIfNoList() {
        jdbcTemplate.update("delete from links");
        List<LinkEntity> links = linkRepository.findStaleLinks(1L);
        assert links.isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void testUpdate_shouldReturnZeroIfNoLink() {
        assertThat(linkRepository.update(10L, OffsetDateTime.now(), OffsetDateTime.now())).isEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback
    void testUpdate_shouldCorrectlyUpdateExistLink() {
        OffsetDateTime checked = OffsetDateTime.now();
        assertThat(linkRepository.update(
            1L,
            checked,
            checked
        )).isEqualTo(1);
        LinkEntity actual = jdbcTemplate.queryForObject(
            "select * from links where id = ?",
            new BeanPropertyRowMapper<>(LinkEntity.class),
            1L
        );
        assertThat(actual.getUpdatedAt()).isEqualToIgnoringNanos(checked);
        assertThat(actual.getCheckedAt()).isEqualToIgnoringNanos(checked);
    }

}
