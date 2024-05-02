package edu.java.client;

import edu.java.client.dto.GitHubBranchResponse;
import edu.java.client.dto.GitHubResponse;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Component
public class GitHubClient {

    private final WebClient webCLient;
    private final Retry retry;

    public GitHubClient(
        @Value("${app.client.github.base-url}")
        @NotBlank @URL String url,
        Retry retry
    ) {
        this.webCLient = WebClient.builder().baseUrl(url).build();
        this.retry = retry;
    }

    public Optional<GitHubResponse> fetchRepository(String owner, String repo) {
        return webCLient.get()
            .uri("/repos/{owner}/{repo}", owner, repo)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(GitHubResponse.class)
            .retryWhen(retry)
            .onErrorResume(Exception.class, e -> Mono.empty())
            .blockOptional();
    }

    public Optional<GitHubBranchResponse[]> fetchRepositoryBranches(String owner, String repo) {
        return webCLient.get()
            .uri("/repos/{owner}/{repo}/branches", owner, repo)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(GitHubBranchResponse[].class)
            .retryWhen(retry)
            .onErrorResume(Exception.class, e -> Mono.empty())
            .blockOptional();
    }
}
