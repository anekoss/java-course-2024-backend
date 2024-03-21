package edu.java.service.jdbc;

import edu.java.controller.exception.AlreadyRegisterException;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.domain.TgChat;
import edu.java.repository.TgChatRepository;
import edu.java.service.TgChatService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcTgChatService implements TgChatService {
    private final TgChatRepository tgChatRepository;

    @Override
    public void register(long tgChatId) throws AlreadyRegisterException {
        Optional<TgChat> optionalChat = tgChatRepository.findByChatId(tgChatId);
        if (optionalChat.isPresent()) {
            throw new AlreadyRegisterException();
        }
        TgChat tgChat = new TgChat(tgChatId);
        tgChatRepository.save(tgChat);
    }

    @Override
    public void unregister(long tgChatId) throws ChatNotFoundException {
        Optional<TgChat> optionalChat = tgChatRepository.findByChatId(tgChatId);
        if (optionalChat.isEmpty()) {
            throw new ChatNotFoundException();
        }
        tgChatRepository.delete(optionalChat.get());
    }
}
