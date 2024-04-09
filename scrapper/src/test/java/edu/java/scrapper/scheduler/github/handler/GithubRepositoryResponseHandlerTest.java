package edu.java.scrapper.scheduler.github.handler;

import edu.java.client.GitHubClient;
import edu.java.client.dto.GitHubResponse;
import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.scheduler.dto.LinkUpdate;
import edu.java.scheduler.dto.UpdateType;
import edu.java.scheduler.github.handler.GithubBranchesResponseHandler;
import edu.java.scheduler.github.handler.GithubRepositoryResponseHandler;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GithubRepositoryResponseHandlerTest {
    private final GitHubClient gitHubClient = Mockito.mock(GitHubClient.class);
    private final GithubRepositoryResponseHandler responseHandler =
        new GithubRepositoryResponseHandler(gitHubClient, null);
    private final GitHubResponse response = new GitHubResponse(
        755879115L,
        "tinkoff-project",
        "anekoss/tinkoff-project",
        OffsetDateTime.parse("2024-02-11T11:13:17Z"),
        OffsetDateTime.parse("2024-02-21T12:54:35Z"),
        OffsetDateTime.parse("2024-02-11T11:13:57Z")
    );
    private final OffsetDateTime checkedAt = OffsetDateTime.parse("2023-02-11T11:13:57Z");
    private final Link link =
        new Link().setUri(URI.create("https://github.com/anekoss/tinkoff-project")).setLinkType(LinkType.GITHUB)
                  .setUpdatedAt(checkedAt).setCheckedAt(checkedAt);
    private final String owner = "anekoss";
    private final String repos = "tinkoff-project";

    @Test
    void testHandle_shouldCorrectlyReturnUpdateLink() {
        OffsetDateTime checkedAt = OffsetDateTime.parse("2023-02-11T11:13:57Z");
        when(gitHubClient.fetchRepository(owner, repos)).thenReturn(Optional.of(response));
        LinkUpdate actual = responseHandler.handle(owner, repos, link);
        assert actual.type() == UpdateType.UPDATE;
        assertThat(actual.link().getUpdatedAt()).isEqualToIgnoringNanos(response.updatedAt());
        assertThat(actual.link().getCheckedAt()).isAfter(checkedAt);
    }

    @Test
    void testHandle_shouldReturnNoUpdateAndUpdateCheckedAtIfNoUpdate() {
        when(gitHubClient.fetchRepository(owner, repos)).thenReturn(Optional.of(response));
        OffsetDateTime updatedAt = response.updatedAt();
        link.setUpdatedAt(response.updatedAt());
        when(gitHubClient.fetchRepository(owner, repos)).thenReturn(Optional.of(response));
        LinkUpdate actual = responseHandler.handle(owner, repos, link);
        assert actual.type() == UpdateType.NO_UPDATE;
        assertThat(actual.link().getUpdatedAt()).isEqualToIgnoringNanos(updatedAt);
        assertThat(actual.link().getCheckedAt()).isAfter(checkedAt);
    }

    @Test
    void testHandle_shouldReturnNoUpdateWithSameLinkIfFetchRepositoryEmpty() {
        when(gitHubClient.fetchRepository(owner, repos)).thenReturn(Optional.empty());
        LinkUpdate actual = responseHandler.handle(owner, repos, link);
        assert actual.type() == UpdateType.NO_UPDATE;
        assertThat(actual.link().getUpdatedAt()).isEqualToIgnoringNanos(checkedAt);
        assertThat(actual.link().getCheckedAt()).isEqualTo(checkedAt);
    }

    @Test
    void testHandleShouldReturnNextHandlerResponseIfHaveUpdate() {
        GithubBranchesResponseHandler githubBranchesResponseHandler = Mockito.mock(GithubBranchesResponseHandler.class);
        when(githubBranchesResponseHandler.handle(owner, repos, link)).thenReturn(new LinkUpdate(
            link,
            UpdateType.UPDATE_ANSWER
        ));
        when(gitHubClient.fetchRepository(owner, repos)).thenReturn(Optional.of(response));
        GithubRepositoryResponseHandler repositoryResponseHandler =
            new GithubRepositoryResponseHandler(gitHubClient, githubBranchesResponseHandler);
        assert repositoryResponseHandler.handle(owner, repos, link).type() == UpdateType.UPDATE_ANSWER;
    }

    @Test
    void testHandleShouldReturnUpdateIfNextHandlerNoUpdate() {
        GithubBranchesResponseHandler githubBranchesResponseHandler = Mockito.mock(GithubBranchesResponseHandler.class);
        when(githubBranchesResponseHandler.handle(owner, repos, link)).thenReturn(new LinkUpdate(
            link,
            UpdateType.NO_UPDATE
        ));
        when(gitHubClient.fetchRepository(owner, repos)).thenReturn(Optional.of(response));
        GithubRepositoryResponseHandler repositoryResponseHandler =
            new GithubRepositoryResponseHandler(gitHubClient, githubBranchesResponseHandler);
        assert repositoryResponseHandler.handle(owner, repos, link).type() == UpdateType.NO_UPDATE;
    }
}
