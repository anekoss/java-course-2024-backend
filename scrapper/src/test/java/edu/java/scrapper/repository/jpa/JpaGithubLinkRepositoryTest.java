package edu.java.scrapper.repository.jpa;

import edu.java.domain.GithubLinkEntity;
import edu.java.domain.LinkEntity;
import edu.java.repository.jpa.JpaGithubLinkRepository;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.IntegrationTest;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JpaGithubLinkRepositoryTest extends IntegrationTest {

    @Autowired
    private JpaGithubLinkRepository githubLinkRepository;
    @Autowired
    private JpaLinkRepository linkRepository;

    @Test
    @Rollback
    @Transactional
    void testFindByLink_Id_shouldCorrectlyReturnGithubLinkIfExist() {
        Optional<GithubLinkEntity> githubLink = githubLinkRepository.findByLinkId(1L);
        assert githubLink.isPresent();
        assertEquals(githubLink.get().getBranchCount(), 2L);
        Optional<LinkEntity> link = linkRepository.findById(1L);
        assert link.isPresent();
        assertEquals(githubLink.get().getLink(), link.get());
    }

    @Test
    @Rollback
    @Transactional
    void testFindByLink_Id_shouldReturnOptionalEmptyIfNoExist() {
        Optional<GithubLinkEntity> githubLink = githubLinkRepository.findByLinkId(13435L);
        assert githubLink.isEmpty();
    }

    @Test
    @Rollback
    @Transactional
    void testExistsByLink_Id_shouldReturnTrueIfExist() {
        assert githubLinkRepository.existsByLinkId(1L);
    }

    @Test
    @Rollback
    @Transactional
    void testExistsByLink_Id_shouldReturnFalseIfNoExist() {
        assert !githubLinkRepository.existsByLinkId(13435L);
    }

}

