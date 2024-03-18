package edu.java.service;

import edu.java.exception.AlreadyRegisterException;
import edu.java.exception.ChatNotFoundException;

public interface ChatService {
    void register(long tgChatId) throws AlreadyRegisterException;

    void unregister(long tgChatId) throws ChatNotFoundException;
}
