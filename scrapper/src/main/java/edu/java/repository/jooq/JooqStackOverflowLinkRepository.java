package edu.java.repository.jooq;

import edu.java.domain.StackOverflowLink;
import edu.java.domain.jooq.tables.records.StackoverflowLinksRecord;
import edu.java.repository.StackOverflowLinkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.domain.jooq.Tables.STACKOVERFLOW_LINKS;

@Repository
@RequiredArgsConstructor
public class JooqStackOverflowLinkRepository implements StackOverflowLinkRepository {
    private final DSLContext dslContext;

    @Override
    @Transactional
    public long add(StackOverflowLink stackOverflowLink) {
        StackoverflowLinksRecord stackoverflowLinksRecord = dslContext.selectFrom(STACKOVERFLOW_LINKS)
                                                                      .where(STACKOVERFLOW_LINKS.LINK_ID.eq(
                                                                          stackOverflowLink.linkId()))
                                                                      .fetchOne();
        dslContext.insertInto(STACKOVERFLOW_LINKS, STACKOVERFLOW_LINKS.LINK_ID, STACKOVERFLOW_LINKS.ANSWER_COUNT)
                  .values(stackOverflowLink.linkId(), stackOverflowLink.answerCount())
                  .onConflict(STACKOVERFLOW_LINKS.LINK_ID)
                  .doUpdate()
                  .set(STACKOVERFLOW_LINKS.ANSWER_COUNT, stackOverflowLink.answerCount())
                  .returning(STACKOVERFLOW_LINKS.ANSWER_COUNT)
                  .fetchOne();
        return stackoverflowLinksRecord != null ? stackoverflowLinksRecord.getAnswerCount()
            : stackOverflowLink.answerCount();
    }

}
