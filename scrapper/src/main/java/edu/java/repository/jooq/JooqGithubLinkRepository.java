package edu.java.repository.jooq;

import edu.java.domain.GithubLink;
import edu.java.domain.jooq.tables.records.GithubLinksRecord;
import edu.java.repository.GithubLinkRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.domain.jooq.Tables.GITHUB_LINKS;

@Repository
@AllArgsConstructor
public class JooqGithubLinkRepository implements GithubLinkRepository {
    private final DSLContext dslContext;

    @Override
    @Transactional
    public long add(GithubLink githubLink) {
        GithubLinksRecord githubLinksRecord = dslContext.selectFrom(GITHUB_LINKS)
                                                        .where(GITHUB_LINKS.LINK_ID.eq(githubLink.linkId()))
                                                        .fetchOne();
        dslContext.insertInto(GITHUB_LINKS, GITHUB_LINKS.LINK_ID, GITHUB_LINKS.BRANCH_COUNT)
                  .values(githubLink.linkId(), githubLink.branchCount())
                  .onConflict(GITHUB_LINKS.LINK_ID)
                  .doUpdate()
                  .set(GITHUB_LINKS.BRANCH_COUNT, githubLink.branchCount())
                  .returning(GITHUB_LINKS.BRANCH_COUNT)
                  .fetchOne();
        return githubLinksRecord != null ? githubLinksRecord.getBranchCount() : githubLink.branchCount();
    }
}
