package edu.java.service.jdbc;

import edu.java.domain.Chat;
import edu.java.repository.ChatRepository;
import edu.java.repository.LinkRepository;
import edu.java.service.ChatService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcChatService implements ChatService {
    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;

    @Override
    public void register(long tgChatId) {
        Optional<Chat> optionalChat = chatRepository.findByChatId(tgChatId);
        if (!optionalChat.isEmpty()) {
            throw new AlreadyRegisterException();
        }
        Chat chat = new Chat(tgChatId);
        chatRepository.save(chat);
    }

    @Override
    public void unregister(long tgChatId) {
        Optional<Chat> optionalChat = chatRepository.findByChatId(tgChatId);
        if (!optionalChat.isEmpty()) {
            throw new ChatNotFoundException();
        }
        chatRepository.delete(optionalChat.get());
    }
}
