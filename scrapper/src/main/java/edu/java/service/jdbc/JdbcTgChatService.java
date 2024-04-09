package edu.java.service.jdbc;

import edu.java.controller.exception.ChatAlreadyExistException;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.domain.TgChatEntity;
import edu.java.repository.TgChatRepository;
import edu.java.service.TgChatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcTgChatService implements TgChatService {
    private final TgChatRepository tgChatRepository;

    @Override
    @Transactional
    public void register(long tgChatId) throws ChatAlreadyExistException {
        TgChatEntity tgChat = new TgChatEntity().setChatId(tgChatId);
        tgChatRepository.add(tgChat);
    }

    @Override
    @Transactional
    public void unregister(long tgChatId) throws ChatNotFoundException {
        TgChatEntity tgChat = new TgChatEntity().setChatId(tgChatId);
        tgChatRepository.remove(tgChat);
    }
}
