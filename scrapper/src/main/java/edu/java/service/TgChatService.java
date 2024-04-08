package edu.java.service;

import edu.java.controller.exception.ChatAlreadyExistException;
import edu.java.controller.exception.ChatNotFoundException;

public interface TgChatService {
    void register(long tgChatId) throws ChatAlreadyExistException;

    void unregister(long tgChatId) throws ChatNotFoundException;
}
