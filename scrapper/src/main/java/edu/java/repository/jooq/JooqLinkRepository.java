package edu.java.repository.jooq;

import edu.java.controller.exception.LinkNotFoundException;
import edu.java.domain.LinkEntity;
import edu.java.domain.LinkType;
import edu.java.domain.jooq.tables.records.LinksRecord;
import edu.java.repository.LinkRepository;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.domain.jooq.Tables.LINKS;

@Repository
@RequiredArgsConstructor
public class JooqLinkRepository implements LinkRepository {

    private final DSLContext dslContext;

    @Override
    @Transactional
    public long add(LinkEntity link) {
        LinksRecord linksRecord =
            dslContext.insertInto(LINKS, LINKS.URI, LINKS.LINK_TYPE, LINKS.UPDATED_AT, LINKS.CHECKED_AT)
                      .values(
                          link.getUri().toString(),
                          link.getLinkType().toString(),
                          LocalDateTime.from(link.getUpdatedAt()),
                          LocalDateTime.from(link.getCheckedAt())
                      )
                      .onConflict(LINKS.URI)
                      .doUpdate()
                      .setAllToExcluded()
                      .returning(LINKS.ID)
                      .fetchOne();
        if (linksRecord == null || linksRecord.getId() == null) {
            throw new IllegalArgumentException();
        }
        return linksRecord.getId();
    }

    @Override
    @Transactional
    public long remove(URI uri) throws LinkNotFoundException {
        LinksRecord linksRecord = dslContext.delete(LINKS)
                                            .where(LINKS.URI.eq(uri.toString()))
                                            .returning(LINKS.ID)
                                            .fetchOne();
        if (linksRecord == null || linksRecord.getId() == null) {
            throw new LinkNotFoundException();
        }
        return linksRecord.getId();
    }

    @Override
    @Transactional
    public List<LinkEntity> findAll() {
        return dslContext.selectFrom(LINKS)
                         .fetch()
                         .map(link -> new LinkEntity(
                             link.getId(),
                             URI.create(link.getUri()),
                             LinkType.valueOf(link.getLinkType()),
                             OffsetDateTime.of(link.getUpdatedAt(), ZoneOffset.UTC),
                             OffsetDateTime.of(link.getCheckedAt(), ZoneOffset.UTC)
                         ))
                         .stream()
                         .toList();
    }

    @Override
    @Transactional
    public Optional<LinkEntity> findByUri(URI uri) {
        LinksRecord linksRecord = dslContext.selectFrom(LINKS).where(LINKS.URI.eq(uri.toString())).fetchOne();
        if (linksRecord == null) {
            return Optional.empty();
        }
        return Optional.of(new LinkEntity(
                linksRecord.getId(),
                URI.create(linksRecord.getUri()),
                LinkType.valueOf(linksRecord.getLinkType()),
                OffsetDateTime.of(linksRecord.getUpdatedAt(), ZoneOffset.UTC),
                OffsetDateTime.of(linksRecord.getCheckedAt(), ZoneOffset.UTC)
            )
        );
    }

    @Override
    @Transactional
    public Optional<LinkEntity> findById(long id) {
        LinksRecord linksRecord = dslContext.selectFrom(LINKS).where(LINKS.ID.eq(id)).fetchOne();
        if (linksRecord == null) {
            return Optional.empty();
        }
        return Optional.of(new LinkEntity(
                linksRecord.getId(),
                URI.create(linksRecord.getUri()),
                LinkType.valueOf(linksRecord.getLinkType()),
                OffsetDateTime.of(linksRecord.getUpdatedAt(), ZoneOffset.UTC),
                OffsetDateTime.of(linksRecord.getCheckedAt(), ZoneOffset.UTC)
            )
        );
    }

    @Override
    @Transactional
    public List<LinkEntity> findStaleLinks(Long limit) {
        return dslContext.selectFrom(LINKS)
                         .orderBy(LINKS.CHECKED_AT.asc())
                         .limit(limit)
                         .fetch().stream().map(link -> new LinkEntity(
                link.getId(),
                URI.create(link.getUri()),
                LinkType.valueOf(link.getLinkType()),
                OffsetDateTime.of(link.getUpdatedAt(), ZoneOffset.UTC),
                OffsetDateTime.of(link.getCheckedAt(), ZoneOffset.UTC)
            )).toList();
    }

    @Override
    @Transactional
    public long update(Long linkId, OffsetDateTime updatedAt, OffsetDateTime checkedAt) {
        return dslContext.update(LINKS)
                         .set(LINKS.UPDATED_AT, updatedAt.toLocalDateTime())
                         .set(LINKS.CHECKED_AT, checkedAt.toLocalDateTime())
                         .where(LINKS.ID.eq(linkId))
                         .execute();
    }
}
