package edu.java.client;

import edu.java.client.dto.GitHubResponse;
import edu.java.client.exception.CustomWebClientException;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.codec.CodecException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Component
public class GitHubClient {

    private final WebClient webCLient;

    public GitHubClient(
            @Value("${app.client.github.base-url}")
            @NotBlank @URL String url
    ) {
        this.webCLient = WebClient.builder().baseUrl(url).build();
    }

    public GitHubResponse fetchRepository(String owner, String repo) throws CustomWebClientException {
        try {
            return webCLient.get()
                            .uri("/repos/{owner}/{repo}", owner, repo)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(GitHubResponse.class)
                            .block();
        } catch (WebClientRequestException | WebClientResponseException | CodecException e) {
            log.warn(e.getMessage());
            throw new CustomWebClientException();
        }
    }
}
