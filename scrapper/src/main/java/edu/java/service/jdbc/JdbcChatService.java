package edu.java.service.jdbc;

import edu.java.domain.Chat;
import edu.java.exception.AlreadyRegisterException;
import edu.java.exception.ChatNotFoundException;
import edu.java.repository.ChatRepository;
import edu.java.service.ChatService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcChatService implements ChatService {
    private final ChatRepository chatRepository;

    @Override
    public void register(long tgChatId) throws AlreadyRegisterException {
        Optional<Chat> optionalChat = chatRepository.findByChatId(tgChatId);
        if (optionalChat.isPresent()) {
            throw new AlreadyRegisterException();
        }
        Chat chat = new Chat(tgChatId);
        chatRepository.save(chat);
    }

    @Override
    public void unregister(long tgChatId) throws ChatNotFoundException {
        Optional<Chat> optionalChat = chatRepository.findByChatId(tgChatId);
        if (optionalChat.isEmpty()) {
            throw new ChatNotFoundException();
        }
        chatRepository.delete(optionalChat.get());
    }
}
