package edu.java.scheduler.github;

import edu.java.client.GitHubClient;
import edu.java.domain.Link;
import edu.java.scheduler.dto.LinkUpdate;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class GithubResponseHandler {
    protected final GitHubClient gitHubClient;
    protected final GithubResponseHandler nextHandler;

    public abstract LinkUpdate handle(String owner, String repos, Link link);

}
