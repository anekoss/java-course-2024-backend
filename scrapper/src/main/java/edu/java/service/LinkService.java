package edu.java.service;

import edu.java.domain.Link;
import edu.java.exception.AlreadyExistException;
import edu.java.exception.ChatNotFoundException;
import java.net.URI;
import java.util.List;
import org.apache.kafka.common.errors.ResourceNotFoundException;

public interface LinkService {
    Link add(long tgChatId, URI url) throws ChatNotFoundException, AlreadyExistException;

    Link remove(long tgChatId, URI url) throws ChatNotFoundException, ResourceNotFoundException;

    List<Link> listAll(long tgChatId);

}
