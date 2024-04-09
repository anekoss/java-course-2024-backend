package edu.java.repository.jpa;

import edu.java.domain.TgChat;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTgChatRepository extends JpaRepository<TgChat, Long> {

    void deleteByChatId(Long chatId);

    Optional<TgChat> findByChatId(Long chatId);

    boolean existsByChatId(Long chatId);
}
