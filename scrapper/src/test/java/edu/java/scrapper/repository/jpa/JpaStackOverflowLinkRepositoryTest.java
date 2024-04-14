package edu.java.scrapper.repository.jpa;

import edu.java.domain.LinkEntity;
import edu.java.domain.StackOverflowLinkEntity;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.repository.jpa.JpaStackOverflowLinkRepository;
import edu.java.scrapper.IntegrationTest;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JpaStackOverflowLinkRepositoryTest extends IntegrationTest {

    @Autowired
    private JpaStackOverflowLinkRepository stackOverflowLinkRepository;
    @Autowired
    private JpaLinkRepository linkRepository;

    @Test
    @Rollback
    @Transactional
    void testFindByLink_Id_shouldCorrectlyReturnGithubLinkIfExist() {
        Optional<StackOverflowLinkEntity> githubLink = stackOverflowLinkRepository.findByLinkId(2L);
        assert githubLink.isPresent();
        assert githubLink.get().getAnswerCount() == 3L;
        Optional<LinkEntity> link = linkRepository.findById(2L);
        assert link.isPresent();
        assertEquals(githubLink.get().getLink(), link.get());
    }

    @Test
    @Rollback
    @Transactional
    void testFindByLink_Id_shouldReturnOptionalEmptyIfNoExist() {
        Optional<StackOverflowLinkEntity> githubLink = stackOverflowLinkRepository.findByLinkId(13435L);
        assert githubLink.isEmpty();
    }

    @Test
    @Rollback
    @Transactional
    void testExistsByLink_Id_shouldReturnTrueIfExist() {
        assert stackOverflowLinkRepository.existsByLinkId(2L);
    }

    @Test
    @Rollback
    @Transactional
    void testExistsByLink_Id_shouldReturnFalseIfNoExist() {
        assert !stackOverflowLinkRepository.existsByLinkId(13435L);
    }

}
