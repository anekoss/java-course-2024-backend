package edu.java.scheduler.github.handler;

import edu.java.client.GitHubClient;
import edu.java.client.dto.GitHubBranchResponse;
import edu.java.domain.GithubLink;
import edu.java.domain.Link;
import edu.java.scheduler.dto.LinkUpdate;
import edu.java.scheduler.dto.UpdateType;
import edu.java.scheduler.github.GithubResponseHandler;
import edu.java.service.LinkService;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class GithubBranchesResponseHandler extends GithubResponseHandler {
    private final LinkService linkService;

    public GithubBranchesResponseHandler(
        GitHubClient gitHubClient,
        GithubResponseHandler nextHandler,
        LinkService linkService
    ) {
        super(gitHubClient, nextHandler);
        this.linkService = linkService;
    }

    @Override
    public LinkUpdate handle(String owner, String repos, Link link) {
        Optional<GitHubBranchResponse[]> gitHubResponse = gitHubClient.fetchRepositoryBranches(owner, repos);
        if (gitHubResponse.isPresent()) {
            GithubLink githubLink = new GithubLink(link.getId(), gitHubResponse.get().length);
            UpdateType type = linkService.updateGithubBranchCount(githubLink);
            if (type != UpdateType.NO_UPDATE) {
                return new LinkUpdate(link, type);
            }
        }
        return nextHandler != null ? nextHandler.handle(owner, repos, link)
            : new LinkUpdate(link, UpdateType.NO_UPDATE);
    }
}
