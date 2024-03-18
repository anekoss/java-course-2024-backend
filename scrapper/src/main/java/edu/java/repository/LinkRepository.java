package edu.java.repository;

import edu.java.domain.Link;
import java.net.URI;
import java.util.List;

public interface LinkRepository {

    int save(Long chatId, Link link);

    int delete(Long chatId, URI uri);

    List<Link> findAll();

    List<Link> findByChatId(Long chatId);

}
