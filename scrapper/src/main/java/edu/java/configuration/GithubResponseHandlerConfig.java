package edu.java.configuration;

import edu.java.client.GitHubClient;
import edu.java.scheduler.github.GithubResponseHandler;
import edu.java.scheduler.github.handler.GithubBranchesResponseHandler;
import edu.java.scheduler.github.handler.GithubRepositoryResponseHandler;
import edu.java.service.LinkService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GithubResponseHandlerConfig {
    @Bean
    GithubResponseHandler responseHandler(GitHubClient gitHubClient, LinkService linkService) {
        return new GithubRepositoryResponseHandler(
            gitHubClient,
            githubBranchesResponseHandler(gitHubClient, linkService)
        );
    }

    @Bean
    GithubBranchesResponseHandler githubBranchesResponseHandler(GitHubClient gitHubClient, LinkService linkService) {
        return new GithubBranchesResponseHandler(gitHubClient, null, linkService);
    }

}
