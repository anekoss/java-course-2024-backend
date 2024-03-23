package edu.java.repository;

import edu.java.domain.TgChat;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTgChatRepository extends JpaRepository<TgChat, Long> {

    Optional<TgChat> findByChatId(Long aLong);
}
