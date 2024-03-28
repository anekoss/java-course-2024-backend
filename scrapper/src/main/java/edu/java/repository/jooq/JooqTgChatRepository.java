package edu.java.repository.jooq;

import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.domain.TgChat;
import edu.java.repository.TgChatRepository;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import static edu.java.domain.jooq.tables.Links.LINKS;
import static edu.java.domain.jooq.tables.TgChatLinks.TG_CHAT_LINKS;
import static edu.java.domain.jooq.tables.TgChats.TG_CHATS;

@Primary
@Repository
@AllArgsConstructor
public class JooqTgChatRepository implements TgChatRepository {
    private final DSLContext dslContext;

    @Override
    @Transactional
    public int save(TgChat tgChat) {
        return dslContext.insertInto(TG_CHATS).set(TG_CHATS.CHAT_ID, tgChat.getChatId())
            .execute();
    }

    @Override
    @Transactional
    public int delete(TgChat tgChat) {
        int count = 0;
        List<Long> linkIds = dslContext.select(TG_CHAT_LINKS.LINK_ID)
            .from(TG_CHAT_LINKS)
            .where(TG_CHAT_LINKS.LINK_ID.in(
                dslContext.select(TG_CHAT_LINKS.LINK_ID)
                    .from(TG_CHAT_LINKS)
                    .join(TG_CHATS).on(TG_CHAT_LINKS.TG_CHAT_ID.eq(TG_CHATS.ID))
                    .where(TG_CHATS.CHAT_ID.eq(tgChat.getChatId()))
            ))
            .groupBy(TG_CHAT_LINKS.LINK_ID)
            .having(DSL.count().eq(1)).fetch().getValues(TG_CHAT_LINKS.LINK_ID);
        for (Long id : linkIds) {
            count += dslContext.deleteFrom(LINKS).where(LINKS.ID.eq(id)).execute();
        }
        count += dslContext.deleteFrom(TG_CHATS).where(TG_CHATS.ID.eq(tgChat.getId())).execute();
        return count;
    }

    @Override
    @Transactional
    public List<TgChat> findAll() {
        List<TgChat> chats =
            dslContext.selectFrom(TG_CHATS).fetch().map(chat -> new TgChat(chat.getId(), chat.getChatId())
            ).stream().toList();
        for (TgChat tgChat : chats) {
            Set<Link> links =
                dslContext.selectDistinct(LINKS.ID, LINKS.URI, LINKS.LINK_TYPE, LINKS.UPDATED_AT, LINKS.CHECKED_AT)
                    .from(LINKS)
                    .join(TG_CHAT_LINKS).on(LINKS.ID.eq(TG_CHAT_LINKS.LINK_ID))
                    .where(TG_CHAT_LINKS.TG_CHAT_ID.eq(tgChat.getId()))
                    .orderBy(LINKS.ID)
                    .fetch().stream().map(link -> new Link(
                        link.value1(),
                        URI.create(link.value2()),
                        LinkType.valueOf(link.value3()),
                        OffsetDateTime.of(link.value4(), ZoneOffset.UTC),
                        OffsetDateTime.of(link.value5(), ZoneOffset.UTC)
                    )).collect(Collectors.toSet());
            tgChat.setLinks(links);
        }
        return chats;
    }

    @Override
    @Transactional
    public Optional<TgChat> findByChatId(Long chatId) {
        Record1<Long> chatRecord = dslContext.select(TG_CHATS.ID)
            .from(TG_CHATS)
            .where(TG_CHATS.CHAT_ID.eq(chatId))
            .fetchOne();
        if (chatRecord == null) {
            return Optional.empty();
        }
        Long tgChatId = chatRecord.value1();
        Set<Link> links =
            dslContext.selectDistinct(LINKS.ID, LINKS.URI, LINKS.LINK_TYPE, LINKS.UPDATED_AT, LINKS.CHECKED_AT)
                .from(LINKS)
                .join(TG_CHAT_LINKS).on(LINKS.ID.eq(TG_CHAT_LINKS.LINK_ID))
                .where(TG_CHAT_LINKS.TG_CHAT_ID.eq(tgChatId))
                .fetch()
                .stream().map(link -> new Link(
                    link.value1(),
                    URI.create(link.value2()),
                    LinkType.valueOf(link.value3()),
                    OffsetDateTime.of(link.value4(), ZoneOffset.UTC),
                    OffsetDateTime.of(link.value5(), ZoneOffset.UTC)
                ))
                .collect(Collectors.toSet());
        TgChat tgChat = new TgChat(tgChatId, chatId);
        tgChat.setLinks(links);
        return Optional.of(tgChat);
    }
}
