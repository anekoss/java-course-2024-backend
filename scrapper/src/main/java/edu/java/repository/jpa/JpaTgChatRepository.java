package edu.java.repository.jpa;

import edu.java.domain.TgChatEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTgChatRepository extends JpaRepository<TgChatEntity, Long> {

    void deleteByChatId(Long chatId);

    Optional<TgChatEntity> findByChatId(Long chatId);

    boolean existsByChatId(Long chatId);
}
