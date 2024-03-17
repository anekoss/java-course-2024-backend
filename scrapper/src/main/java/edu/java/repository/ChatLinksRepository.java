package edu.java.repository;

import edu.java.domain.Link;
import java.util.List;

public interface ChatLinksRepository {

    Link findByLinkIdAndChatId(Long linkId, Long chatId);

    List<Link> findAllByChatId(Long chatId);

    int deleteByChatId(Long chatId);

     int save(Long linkId, Long chatId);
}
