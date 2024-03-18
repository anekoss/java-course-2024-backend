package edu.java.client;

import edu.java.client.dto.GitHubResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class GitHubClient {

    private final WebClient webCLient;

    public GitHubClient(@Value("${app.client.github.base-url}") @NotBlank @URL String url) {
        this.webCLient = WebClient.builder().baseUrl(url).build();
    }

    public GitHubResponse fetchRepository(String owner, String repo) {
        return webCLient.get()
                        .uri("/repos/{owner}/{repo}", owner, repo)
                        .retrieve()
                        .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                            throw new HttpServerErrorException(clientResponse.statusCode());
                        })
                        .bodyToMono(GitHubResponse.class)
                        .onErrorMap(error -> {
                            throw new IllegalArgumentException(error.getMessage());
                        })
                        .block();
    }

}
