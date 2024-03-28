package edu.java.repository.jooq;

import edu.java.repository.GithubLinkRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import static edu.java.domain.jooq.Tables.GITHUB_LINKS;

@Primary
@Repository
@AllArgsConstructor
public class JooqGithubLinkRepository implements GithubLinkRepository {
    private final DSLContext dslContext;

    @Override
    @Transactional
    public Optional<Long> findGithubBranchCountByLinkId(Long linkId) {
        Long count = dslContext.select(GITHUB_LINKS.BRANCH_COUNT)
            .from(GITHUB_LINKS)
            .where(GITHUB_LINKS.LINK_ID.eq(linkId))
            .fetchOneInto(Long.class);
        return Optional.ofNullable(count);
    }

    @Override
    @Transactional
    public int add(Long linkId, Long branchCount) {
        Long count = dslContext.selectCount()
            .from(GITHUB_LINKS)
            .where(GITHUB_LINKS.LINK_ID.eq(linkId))
            .fetchOneInto(Long.class);
        if (count != null && count != 0L) {
            return 0;
        }
        return dslContext.insertInto(GITHUB_LINKS)
            .set(GITHUB_LINKS.LINK_ID, linkId)
            .set(GITHUB_LINKS.BRANCH_COUNT, branchCount)
            .execute();
    }

    @Override
    @Transactional
    public int update(Long linkId, Long branchCount) {
        return dslContext.update(GITHUB_LINKS)
            .set(GITHUB_LINKS.BRANCH_COUNT, branchCount)
            .where(GITHUB_LINKS.LINK_ID.eq(linkId))
            .execute();
    }
}
