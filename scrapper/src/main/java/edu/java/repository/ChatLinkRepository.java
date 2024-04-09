package edu.java.repository;

import edu.java.controller.exception.LinkAlreadyExistException;
import edu.java.controller.exception.LinkNotFoundException;
import edu.java.domain.ChatLink;
import java.util.List;

public interface ChatLinkRepository {

    long add(ChatLink chatLink) throws LinkAlreadyExistException;

    long remove(ChatLink chatLink) throws LinkNotFoundException;

    List<ChatLink> findByTgChatId(long tgChatId);

    List<ChatLink> findByLinkId(long linkId);
}
