package edu.java.scrapper.repository.jpa;

import edu.java.domain.TgChatEntity;
import edu.java.repository.jpa.JpaTgChatRepository;
import edu.java.scrapper.IntegrationTest;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JpaTgChatRepositoryTest extends IntegrationTest {
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    private JpaTgChatRepository tgChatRepository;

    @Test
    @Rollback
    @Transactional
    void testDeleteByChatId_shouldCorrectlyDelete() {
        long chatId = 555555L;
        tgChatRepository.deleteByChatId(chatId);
        assert !tgChatRepository.existsByChatId(chatId);
    }

    @Test
    @Rollback
    @Transactional
    void testFindByChatId_shouldCorrectlyFindChatIfExist() {
        Optional<TgChatEntity> tgChat = tgChatRepository.findByChatId(444444L);
        assert tgChat.isPresent();
        assertEquals(tgChat.get().getChatId(), 444444L);
        assert tgChat.get().getLinks().size() == 1;
    }

    @Test
    @Rollback
    @Transactional
    void testFindByChatId_shouldCReturnOptionalEmptyIfNoExist() {
        Optional<TgChatEntity> tgChat = tgChatRepository.findByChatId(458923L);
        assert tgChat.isEmpty();
    }

    @Test
    @Rollback
    @Transactional
    void testExistsByChatId_shouldReturnTrueIfChatExist() {
        assert tgChatRepository.existsByChatId(444444L);
    }

    @Test
    @Rollback
    @Transactional
    void testExistsByChatId_shouldReturnFalseIfChatNpExist() {
        assert !tgChatRepository.existsByChatId(458923L);
    }
}
