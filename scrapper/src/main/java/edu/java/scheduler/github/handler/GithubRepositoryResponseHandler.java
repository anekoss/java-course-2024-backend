package edu.java.scheduler.github.handler;

import edu.java.client.GitHubClient;
import edu.java.client.dto.GitHubResponse;
import edu.java.domain.Link;
import edu.java.scheduler.dto.LinkUpdate;
import edu.java.scheduler.dto.UpdateType;
import edu.java.scheduler.github.GithubResponseHandler;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class GithubRepositoryResponseHandler extends GithubResponseHandler {

    public GithubRepositoryResponseHandler(
        GitHubClient gitHubClient,
        GithubResponseHandler nextHandler
    ) {
        super(gitHubClient, nextHandler);
    }

    @Override
    public LinkUpdate handle(String owner, String repos, Link link) {
        Optional<GitHubResponse> gitHubResponse = gitHubClient.fetchRepository(owner, repos);
        if (gitHubResponse.isPresent() && gitHubResponse.get().updatedAt() != null) {
            OffsetDateTime updatedAt = gitHubResponse.get().updatedAt();
            if (updatedAt.isAfter(link.getUpdatedAt())) {
                link.setUpdatedAt(updatedAt);
                return nextHandler != null ? nextHandler.handle(owner, repos, link) :
                    new LinkUpdate(link, UpdateType.UPDATE);
            }
        }
        return new LinkUpdate(link, UpdateType.NO_UPDATE);
    }
}
