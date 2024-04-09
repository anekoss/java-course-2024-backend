package edu.java.repository;

import edu.java.controller.exception.ChatAlreadyExistException;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.domain.TgChatEntity;
import java.util.List;
import java.util.Optional;

public interface TgChatRepository {

    long add(TgChatEntity tgChat) throws ChatAlreadyExistException;

    long remove(TgChatEntity tgChat) throws ChatNotFoundException;

    List<TgChatEntity> findAll();

    TgChatEntity findByChatId(Long chatId) throws ChatNotFoundException;

    Optional<TgChatEntity> findById(Long id);

}
