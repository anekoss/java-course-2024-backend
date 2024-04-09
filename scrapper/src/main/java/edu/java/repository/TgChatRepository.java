package edu.java.repository;

import edu.java.controller.exception.ChatAlreadyExistException;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.domain.TgChat;
import java.util.List;
import java.util.Optional;

public interface TgChatRepository {

    long add(TgChat tgChat) throws ChatAlreadyExistException;

    long remove(TgChat tgChat) throws ChatNotFoundException;

    List<TgChat> findAll();

    TgChat findByChatId(Long chatId) throws ChatNotFoundException;

    Optional<TgChat> findById(Long id);

}
