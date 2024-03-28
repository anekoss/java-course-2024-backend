package edu.java.repository.jooq;

import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.domain.TgChat;
import edu.java.repository.LinkRepository;
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
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import static edu.java.domain.jooq.Tables.LINKS;
import static edu.java.domain.jooq.Tables.TG_CHAT_LINKS;
import static edu.java.domain.jooq.tables.TgChats.TG_CHATS;

@Primary
@Repository
@AllArgsConstructor
public class JooqLinkRepository implements LinkRepository {

    private final DSLContext dslContext;

    @Override
    @Transactional
    public int save(Long tgChatId, Link link) {
        Optional<Long> linkId = findIdByUri(link.getUri());
        if (linkId.isPresent()) {
            Long count = dslContext.selectCount()
                .from(TG_CHAT_LINKS)
                .where(TG_CHAT_LINKS.TG_CHAT_ID.eq(tgChatId).and(TG_CHAT_LINKS.LINK_ID.eq(linkId.get())))
                .fetchOne(0, Long.class);
            if (count != null && count != 0L) {
                return 0;
            }
        }
        if (linkId.isEmpty()) {
            dslContext.insertInto(LINKS, LINKS.URI, LINKS.LINK_TYPE, LINKS.UPDATED_AT, LINKS.CHECKED_AT)
                .values(
                    link.getUri().toString(),
                    link.getLinkType().toString(),
                    link.getUpdatedAt().toLocalDateTime(),
                    link.getCheckedAt().toLocalDateTime()
                )
                .execute();
            linkId = findIdByUri(link.getUri());
        }
        return dslContext.insertInto(TG_CHAT_LINKS, TG_CHAT_LINKS.TG_CHAT_ID, TG_CHAT_LINKS.LINK_ID)
            .values(tgChatId, linkId.get())
            .execute();
    }

    @Override
    @Transactional
    public int delete(Long tgChatId, URI uri) {
        Optional<Long> linkId = findIdByUri(uri);
        if (linkId.isPresent()) {
            Long countLinks = dslContext.selectCount()
                .from(TG_CHAT_LINKS)
                .where(TG_CHAT_LINKS.LINK_ID.eq(linkId.get()))
                .fetchOne(0, Long.class);
            if (countLinks != null && countLinks == 1L) {
                return dslContext.deleteFrom(LINKS)
                    .where(LINKS.ID.eq(linkId.get()))
                    .execute();
            } else {
                return dslContext.deleteFrom(TG_CHAT_LINKS)
                    .where(TG_CHAT_LINKS.TG_CHAT_ID.eq(tgChatId).and(TG_CHAT_LINKS.LINK_ID.eq(linkId.get())))
                    .execute();
            }
        }
        return 0;
    }

    @Override
    @Transactional
    public List<Link> findAll() {
        List<Link> links = dslContext.selectFrom(LINKS).fetch().map(link -> new Link(
                link.getId(),
                URI.create(link.getUri()),
                LinkType.valueOf(link.getLinkType()),
                OffsetDateTime.of(link.getUpdatedAt(), ZoneOffset.UTC),
                OffsetDateTime.of(link.getCheckedAt(), ZoneOffset.UTC)
            )
        );
        for (Link link : links) {
            Set<TgChat> tgChats = dslContext.select(TG_CHATS.ID, TG_CHATS.CHAT_ID)
                .from(TG_CHATS)
                .join(TG_CHAT_LINKS).on(TG_CHATS.ID.eq(TG_CHAT_LINKS.TG_CHAT_ID))
                .where(TG_CHAT_LINKS.LINK_ID.eq(link.getId()))
                .fetch()
                .stream()
                .map(tgChatRecord -> new TgChat(tgChatRecord.get(TG_CHATS.ID), tgChatRecord.get(TG_CHATS.CHAT_ID)))
                .collect(
                    Collectors.toSet());
            link.setTgChats(tgChats);
        }
        return links;
    }

    @Override
    @Transactional
    public Optional<Long> findIdByUri(URI uri) {
        Record1<Long> result = dslContext.select(LINKS.ID)
            .from(LINKS)
            .where(LINKS.URI.eq(uri.toString()))
            .fetchOne();
        return Optional.ofNullable(result).map(id -> id.get(LINKS.ID));
    }

    @Override
    @Transactional
    public List<Link> findStaleLinks(Long limit) {
        return dslContext.selectFrom(LINKS)
            .orderBy(LINKS.CHECKED_AT.asc())
            .limit(limit.intValue())
            .fetch().stream().map(link -> new Link(
                link.getId(),
                URI.create(link.getUri()),
                LinkType.valueOf(link.getLinkType()),
                OffsetDateTime.of(link.getUpdatedAt(), ZoneOffset.UTC),
                OffsetDateTime.of(link.getCheckedAt(), ZoneOffset.UTC)
            )).toList();
    }

    @Override
    @Transactional
    public int update(Long linkId, OffsetDateTime updatedAt, OffsetDateTime checkedAt) {
        return dslContext.update(LINKS)
            .set(LINKS.UPDATED_AT, updatedAt.toLocalDateTime())
            .set(LINKS.CHECKED_AT, checkedAt.toLocalDateTime())
            .where(LINKS.ID.eq(linkId))
            .execute();
    }
}
