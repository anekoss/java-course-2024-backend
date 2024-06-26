package edu.java.scheduler.github.handler;

import edu.java.client.GitHubClient;
import edu.java.client.dto.GitHubResponse;
import edu.java.domain.LinkEntity;
import edu.java.scheduler.dto.LinkUpdate;
import edu.java.scheduler.dto.UpdateType;
import edu.java.scheduler.github.GithubResponseHandler;
import java.time.OffsetDateTime;
import java.util.Optional;

public class GithubRepositoryResponseHandler extends GithubResponseHandler {

    public GithubRepositoryResponseHandler(
        GitHubClient gitHubClient,
        GithubResponseHandler nextHandler
    ) {
        super(gitHubClient, nextHandler);
    }

    @Override
    public LinkUpdate handle(String owner, String repos, LinkEntity link) {
        Optional<GitHubResponse> gitHubResponse = gitHubClient.fetchRepository(owner, repos);
        UpdateType type = UpdateType.NO_UPDATE;
        if (gitHubResponse.isPresent() && gitHubResponse.get().updatedAt() != null) {
            OffsetDateTime updatedAt = gitHubResponse.get().updatedAt();
            link.setCheckedAt(OffsetDateTime.now());
            if (updatedAt.isAfter(link.getUpdatedAt())) {
                link.setUpdatedAt(updatedAt);
                type = UpdateType.UPDATE;
            }
        }
        return nextHandler != null ? nextHandler.handle(owner, repos, link)
            : new LinkUpdate(link, type);
    }
}
