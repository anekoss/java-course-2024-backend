package edu.java.scrapper.service.updateChecker;

import edu.java.client.GitHubClient;
import edu.java.client.dto.GitHubBranchResponse;
import edu.java.client.dto.GitHubResponse;
import edu.java.client.exception.BadResponseBodyException;
import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.domain.UpdateType;
import edu.java.repository.GithubLinkRepository;
import edu.java.service.updateChecker.JdbcGithubUpdateChecker;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.annotation.Rollback;
import static edu.java.domain.UpdateType.NEW_ANSWER;
import static edu.java.domain.UpdateType.NO_UPDATE;
import static edu.java.domain.UpdateType.UPDATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GithubUpdateCheckerTest {
    private final GitHubClient gitHubClient = Mockito.mock(GitHubClient.class);
    private final GithubLinkRepository githubLinkRepository = Mockito.mock(GithubLinkRepository.class);
    private final JdbcGithubUpdateChecker updateChecker =
        new JdbcGithubUpdateChecker(gitHubClient, githubLinkRepository);

    private final GitHubBranchResponse branchResponse = new GitHubBranchResponse(new String[] {"hw", "hw1"});
    private final Link link = new Link(URI.create("https://github.com/anekoss/tinkoff-project"), LinkType.GITHUB);

    @BeforeEach
    void init() throws BadResponseBodyException {
        when(gitHubClient.fetchRepositoryBranches(anyString(), anyString())).thenReturn(branchResponse);
        when(githubLinkRepository.add(any(), any())).thenReturn(1);
        when(githubLinkRepository.update(any(), any())).thenReturn(1);
        GitHubResponse response = new GitHubResponse(
            755879115L,
            "tinkoff-project",
            "anekoss/tinkoff-project",
            OffsetDateTime.parse("2024-02-11T11:13:17Z"),
            OffsetDateTime.parse("2024-02-21T12:54:35Z"),
            OffsetDateTime.parse("2024-02-11T11:13:57Z")
        );
        when(gitHubClient.fetchRepository(anyString(), anyString())).thenReturn(response);
    }

    @Test
    @Rollback
    @Transactional
    void testCheckBranchesShouldAddBranchCount() {
        when(githubLinkRepository.findGithubBranchCountByLinkId(any())).thenReturn(any()).thenReturn(Optional.empty());
        assertThat(updateChecker.checkBranches(new String[] {"anekoss", "tinkoff-project"}, link, UPDATE)).isEqualTo(
            UPDATE);
    }

    @Test
    @Rollback
    @Transactional
    void testCheckBranchesShouldReturnNewAnswer() {
        when(githubLinkRepository.findGithubBranchCountByLinkId(any())).thenReturn(any()).thenReturn(Optional.of(0L));
        assertThat(updateChecker.checkBranches(new String[] {"anekoss", "tinkoff-project"}, link, UPDATE)).isEqualTo(
            NEW_ANSWER);
    }

    @Test
    @Rollback
    @Transactional
    void testCheckBranchesShouldReturnUpdate() {
        when(githubLinkRepository.findGithubBranchCountByLinkId(any())).thenReturn(any()).thenReturn(Optional.of(2L));
        assertThat(updateChecker.checkBranches(new String[] {"anekoss", "tinkoff-project"}, link, UPDATE)).isEqualTo(
            UPDATE);
    }

    @Test
    @Rollback
    @Transactional
    void testCheckBranchesShouldReturnNoUpdate() {
        when(githubLinkRepository.findGithubBranchCountByLinkId(any())).thenReturn(any()).thenReturn(Optional.of(2L));
        assertThat(updateChecker.checkBranches(new String[] {"anekoss", "tinkoff-project"}, link, NO_UPDATE)).isEqualTo(
            NO_UPDATE);
    }

    @Test
    @Rollback
    @Transactional
    void testCheckBranchesShouldThrowException() throws BadResponseBodyException {
        when(githubLinkRepository.findGithubBranchCountByLinkId(any())).thenReturn(any()).thenReturn(Optional.of(2L));
        when(gitHubClient.fetchRepositoryBranches(anyString(), anyString())).thenThrow(BadResponseBodyException.class);
        assertThat(updateChecker.checkBranches(new String[] {"anekoss", "tinkoff-project"}, link, UPDATE)).isEqualTo(
            UPDATE);
    }

    @Test
    void testCheckShouldUpdate() {
        when(githubLinkRepository.findGithubBranchCountByLinkId(any())).thenReturn(any()).thenReturn(Optional.empty());
        OffsetDateTime checkedAt = OffsetDateTime.now();
        link.setUpdatedAt(OffsetDateTime.parse("2023-02-11T11:13:57Z"));
        link.setCheckedAt(checkedAt);
        Map.Entry<Link, UpdateType> updatedLink = updateChecker.check(link);
        assertThat(updatedLink.getValue()).isEqualTo(UPDATE);
        assertThat(updatedLink.getKey().getUpdatedAt()).isEqualTo(OffsetDateTime.parse("2024-02-11T11:13:57Z"));
        assertThat(updatedLink.getKey().getCheckedAt()).isAfter(checkedAt);
    }

    @Test
    void testCheckShouldReturnNewAnswer() {
        when(githubLinkRepository.findGithubBranchCountByLinkId(any())).thenReturn(any()).thenReturn(Optional.of(0L));
        OffsetDateTime checkedAt = OffsetDateTime.now();
        link.setUpdatedAt(OffsetDateTime.parse("2023-02-11T11:13:57Z"));
        link.setCheckedAt(checkedAt);
        Map.Entry<Link, UpdateType> updatedLink = updateChecker.check(link);
        assertThat(updatedLink.getValue()).isEqualTo(NEW_ANSWER);
        assertThat(updatedLink.getKey().getUpdatedAt()).isEqualTo(OffsetDateTime.parse("2024-02-11T11:13:57Z"));
        assertThat(updatedLink.getKey().getCheckedAt()).isAfter(checkedAt);
    }

    @Test
    void testCheckShouldNotUpdate() {
        when(githubLinkRepository.findGithubBranchCountByLinkId(any())).thenReturn(any()).thenReturn(Optional.empty());

        OffsetDateTime checkedAt = OffsetDateTime.now();
        link.setUpdatedAt(OffsetDateTime.parse("2023-02-11T11:13:57Z"));
        link.setCheckedAt(checkedAt);
        Map.Entry<Link, UpdateType> updatedLink = updateChecker.check(link);
        assertThat(updatedLink.getValue()).isEqualTo(NO_UPDATE);
        assertThat(updatedLink.getKey().getUpdatedAt()).isEqualTo(link.getUpdatedAt());
        assertThat(updatedLink.getKey().getCheckedAt()).isAfter(checkedAt);
    }

    @Test
    void testCheckShouldReturnInputLink() throws BadResponseBodyException {
        when(githubLinkRepository.findGithubBranchCountByLinkId(any())).thenReturn(any()).thenReturn(Optional.empty());
        when(gitHubClient.fetchRepository(anyString(), anyString())).thenThrow(BadResponseBodyException.class);
        OffsetDateTime checkedAt = OffsetDateTime.now();
        link.setUpdatedAt(OffsetDateTime.parse("2023-02-11T11:13:57Z"));
        link.setCheckedAt(checkedAt);
        Map.Entry<Link, UpdateType> updatedLink = updateChecker.check(link);
        assertThat(updatedLink.getValue()).isEqualTo(NO_UPDATE);
        assertThat(updatedLink.getKey().getUpdatedAt()).isEqualTo(link.getUpdatedAt());
        assertThat(updatedLink.getKey().getCheckedAt()).isEqualTo(link.getCheckedAt());
    }

}
