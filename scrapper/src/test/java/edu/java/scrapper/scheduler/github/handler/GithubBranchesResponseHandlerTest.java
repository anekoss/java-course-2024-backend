package edu.java.scrapper.scheduler.github.handler;

import edu.java.client.GitHubClient;
import edu.java.client.dto.GitHubBranchResponse;
import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.scheduler.dto.LinkUpdate;
import edu.java.scheduler.dto.UpdateType;
import edu.java.scheduler.github.GithubResponseHandler;
import edu.java.scheduler.github.handler.GithubBranchesResponseHandler;
import edu.java.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GithubBranchesResponseHandlerTest {
    private final GitHubClient gitHubClient = Mockito.mock(GitHubClient.class);
    private final LinkService linkService = Mockito.mock(LinkService.class);
    private final GithubResponseHandler nextHandler = Mockito.mock(GithubResponseHandler.class);
    private final GithubResponseHandler responseHandler =
        new GithubBranchesResponseHandler(gitHubClient, nextHandler, linkService);
    private final GitHubBranchResponse[] response =
        new GitHubBranchResponse[] {new GitHubBranchResponse("hw1"), new GitHubBranchResponse("hw2"),
            new GitHubBranchResponse("hw_2"), new GitHubBranchResponse("hw3")};

    private final Link link =
        new Link().setId(1L).setUri(URI.create("https://github.com/anekoss/tinkoff-project"))
                  .setLinkType(LinkType.GITHUB)
                  .setUpdatedAt(OffsetDateTime.now()).setCheckedAt(OffsetDateTime.now());
    private final String owner = "anekoss";
    private final String repos = "tinkoff-project";

    @Test
    void testHandle_shouldCorrectlyReturnUpdateBranchIfHaveUpdateAndNextHandlerIsNull() {
        when(gitHubClient.fetchRepositoryBranches(owner, repos)).thenReturn(Optional.of(response));
        when(linkService.updateGithubBranchCount(any())).thenReturn(UpdateType.UPDATE_BRANCH);
        GithubResponseHandler handler = new GithubBranchesResponseHandler(gitHubClient, null, linkService);
        LinkUpdate actual = handler.handle(owner, repos, link);
        assert actual.type() == UpdateType.UPDATE_BRANCH;
    }

    @Test
    void testHandle_shouldReturnNoUpdateIfNoUpdateAndNextHandlerIsNull() {
        when(gitHubClient.fetchRepositoryBranches(owner, repos)).thenReturn(Optional.of(response));
        when(linkService.updateGithubBranchCount(any())).thenReturn(UpdateType.NO_UPDATE);
        GithubResponseHandler handler = new GithubBranchesResponseHandler(gitHubClient, null, linkService);
        LinkUpdate actual = handler.handle(owner, repos, link);
        assert actual.type() == UpdateType.NO_UPDATE;
    }

    @Test
    void testHandle_shouldCorrectlyReturnUpdateBranchIfHaveUpdateAndNextHandlerNoUpdate() {
        when(gitHubClient.fetchRepositoryBranches(owner, repos)).thenReturn(Optional.of(response));
        when(linkService.updateGithubBranchCount(any())).thenReturn(UpdateType.UPDATE_BRANCH);
        when(nextHandler.handle(owner, repos, link)).thenReturn(new LinkUpdate(
            link,
            UpdateType.NO_UPDATE
        ));
        LinkUpdate actual = responseHandler.handle(owner, repos, link);
        assert actual.type() == UpdateType.UPDATE_BRANCH;
    }

    @Test
    void testHandle_shouldReturnNextHandlerResponseIfNoUpdate() {
        when(gitHubClient.fetchRepositoryBranches(owner, repos)).thenReturn(Optional.of(response));
        when(linkService.updateGithubBranchCount(any())).thenReturn(UpdateType.NO_UPDATE);
        when(nextHandler.handle(owner, repos, link)).thenReturn(new LinkUpdate(
            link,
            UpdateType.UPDATE
        ));
        LinkUpdate actual = responseHandler.handle(owner, repos, link);
        assert actual.type() == UpdateType.UPDATE;
    }

    @Test
    void testHandle_shouldReturnNoUpdateIfFetchRepositoryEmptyAndNextHandlerIsNull() {
        when(gitHubClient.fetchRepository(owner, repos)).thenReturn(Optional.empty());
        GithubResponseHandler handler = new GithubBranchesResponseHandler(gitHubClient, null, linkService);
        LinkUpdate actual = handler.handle(
            owner, repos,
            link
        );
        assert actual.type() == UpdateType.NO_UPDATE;
    }

    @Test
    void testHandle_shouldReturnNextHandlerResponseIfFetchRepositoryEmpty() {
        when(gitHubClient.fetchRepository(owner, repos)).thenReturn(Optional.empty());
        when(nextHandler.handle(owner, repos, link)).thenReturn(new LinkUpdate(
            link,
            UpdateType.UPDATE
        ));
        LinkUpdate actual = responseHandler.handle(owner, repos, link);
        when(nextHandler).thenReturn(null);
        assert actual.type() == UpdateType.UPDATE;
    }
}
