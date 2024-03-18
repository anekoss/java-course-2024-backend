package edu.java.repository;

import edu.java.domain.Chat;

import java.util.List;
import java.util.Optional;

public interface ChatRepository {

    int save(Chat chat);

    int delete(Chat chat);

    List<Chat> findAll();

    Optional<Chat> findByChatId(Long chatId);
}
