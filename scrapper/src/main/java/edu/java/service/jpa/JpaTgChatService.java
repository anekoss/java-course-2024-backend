package edu.java.service.jpa;

import edu.java.controller.exception.ChatAlreadyExistException;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.domain.TgChat;
import edu.java.repository.jpa.JpaTgChatRepository;
import edu.java.service.TgChatService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JpaTgChatService implements TgChatService {
    private final JpaTgChatRepository tgChatRepository;

    @Override
    public void register(long tgChatId) throws ChatAlreadyExistException {
        if (!tgChatRepository.existsByChatId(tgChatId)) {
            tgChatRepository.saveAndFlush(new TgChat().setChatId(tgChatId));
        } else {
            throw new ChatAlreadyExistException();
        }
    }

    @Override
    public void unregister(long tgChatId) throws ChatNotFoundException {
        if (tgChatRepository.existsByChatId(tgChatId)) {
            tgChatRepository.deleteByChatId(tgChatId);
        } else {
            throw new ChatNotFoundException();
        }
    }
}
