package edu.java.repository.jooq;

import edu.java.repository.StackOverflowLinkRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import static edu.java.domain.jooq.Tables.STACKOVERFLOW_LINKS;

@Primary
@Repository
@AllArgsConstructor
public class JooqStackOverflowLinkRepository implements StackOverflowLinkRepository {
    private final DSLContext dslContext;

    @Override
    public Optional<Long> findStackOverflowAnswerCountByLinkId(Long linkId) {
        Long count = dslContext.select(STACKOVERFLOW_LINKS.ANSWER_COUNT)
            .from(STACKOVERFLOW_LINKS)
            .where(STACKOVERFLOW_LINKS.LINK_ID.eq(linkId))
            .fetchOneInto(Long.class);
        return Optional.ofNullable(count);
    }

    @Override
    public int add(Long linkId, Long answerCount) {
        Long count = dslContext.selectCount()
            .from(STACKOVERFLOW_LINKS)
            .where(STACKOVERFLOW_LINKS.LINK_ID.eq(linkId))
            .fetchOneInto(Long.class);
        if (count != null && count != 0L) {
            return 0;
        }
        return dslContext.insertInto(STACKOVERFLOW_LINKS)
            .set(STACKOVERFLOW_LINKS.LINK_ID, linkId)
            .set(STACKOVERFLOW_LINKS.ANSWER_COUNT, answerCount)
            .execute();
    }

    @Override
    public int update(Long linkId, Long answerCount) {
        return dslContext.update(STACKOVERFLOW_LINKS)
            .set(STACKOVERFLOW_LINKS.ANSWER_COUNT, answerCount)
            .where(STACKOVERFLOW_LINKS.LINK_ID.eq(linkId))
            .execute();
    }
}
