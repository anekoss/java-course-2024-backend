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
    GithubResponseHandler nextHandler() {
        return null;
    }

    @Bean
    GithubBranchesResponseHandler githubBranchesResponseHandler(LinkService linkService, GitHubClient gitHubClient) {
        return new GithubBranchesResponseHandler(gitHubClient, null, linkService);
    }

    @Bean GithubResponseHandler responseHandler(
        LinkService linkService,
        GitHubClient gitHubClient
    ) {
        return new GithubRepositoryResponseHandler(
            gitHubClient,
            githubBranchesResponseHandler(linkService, gitHubClient)
        );
    }

}
