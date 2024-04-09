package edu.java.repository.jooq;

import edu.java.controller.exception.LinkAlreadyExistException;
import edu.java.controller.exception.LinkNotFoundException;
import edu.java.domain.ChatLink;
import edu.java.domain.jooq.tables.records.TgChatLinksRecord;
import edu.java.repository.ChatLinkRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.domain.jooq.Tables.TG_CHAT_LINKS;

@Repository
@RequiredArgsConstructor
public class JooqChatLinkRepository implements ChatLinkRepository {
    private final DSLContext dslContext;

    @Override
    @Transactional
    public long add(ChatLink chatLink) throws LinkAlreadyExistException {
        TgChatLinksRecord tgChatLinksRecord =
            dslContext.insertInto(TG_CHAT_LINKS, TG_CHAT_LINKS.TG_CHAT_ID, TG_CHAT_LINKS.LINK_ID)
                      .values(chatLink.tgChatId(), chatLink.linkId())
                      .onConflictDoNothing()
                      .returning(TG_CHAT_LINKS.ID)
                      .fetchOne();
        if (tgChatLinksRecord == null || tgChatLinksRecord.getId() == null) {
            throw new LinkAlreadyExistException();
        }
        return tgChatLinksRecord.getId();
    }

    @Override
    @Transactional
    public long remove(ChatLink chatLink) throws LinkNotFoundException {
        TgChatLinksRecord tgChatLinksRecord = dslContext.deleteFrom(TG_CHAT_LINKS)
                                                        .where(TG_CHAT_LINKS.LINK_ID.eq(chatLink.linkId()))
                                                        .and(TG_CHAT_LINKS.TG_CHAT_ID.eq(chatLink.tgChatId()))
                                                        .returning(TG_CHAT_LINKS.ID).fetchOne();
        if (tgChatLinksRecord == null || tgChatLinksRecord.getId() == null) {
            throw new LinkNotFoundException();
        }
        return tgChatLinksRecord.getId();
    }

    @Override
    @Transactional
    public List<ChatLink> findByTgChatId(long tgChatId) {
        return dslContext.selectFrom(TG_CHAT_LINKS)
                         .where(TG_CHAT_LINKS.TG_CHAT_ID.eq(tgChatId))
                         .fetch()
                         .map(chatLinks -> new ChatLink(chatLinks.getTgChatId(), chatLinks.getLinkId()))
                         .stream()
                         .toList();
    }

    @Override
    @Transactional
    public List<ChatLink> findByLinkId(long linkId) {
        return dslContext.selectFrom(TG_CHAT_LINKS)
                         .where(TG_CHAT_LINKS.LINK_ID.eq(linkId))
                         .fetch()
                         .map(chatLinks -> new ChatLink(chatLinks.getTgChatId(), chatLinks.getLinkId()))
                         .stream()
                         .toList();
    }

}
