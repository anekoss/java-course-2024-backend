package edu.java.repository;

import edu.java.domain.Chat;
import java.util.List;

public interface ChatRepository {

    int save(Chat chat);

    int delete(Long id);

    Chat findByChatId(Long chatId);

    List<Chat> findAll();
}
