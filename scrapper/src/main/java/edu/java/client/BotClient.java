package edu.java.client;

import edu.java.client.dto.LinkUpdateRequest;
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
public class BotClient {

    private final WebClient webCLient;
    private final Retry retry;

    public BotClient(
        @Value("${app.client.botClient.base-url}")
        @NotBlank @URL String url,
        Retry retry
    ) {
        this.webCLient = WebClient.builder().baseUrl(url).build();
        this.retry = retry;
    }

    public Optional<String> linkUpdates(LinkUpdateRequest request) {
        return webCLient
            .post()
            .uri("/updates")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), LinkUpdateRequest.class)
            .retrieve()
            .bodyToMono(String.class)
            .retryWhen(retry)
            .onErrorResume(Exception.class, e -> {
                log.warn(e.getMessage());
                return Mono.empty();
            })
            .blockOptional();
    }
}
