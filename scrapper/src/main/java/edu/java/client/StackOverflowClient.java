package edu.java.client;

import edu.java.client.dto.StackOverflowResponse;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class StackOverflowClient {
    private final WebClient webClient;

    public StackOverflowClient(
        @Value("${app.client.stackOverflow.base-url}")
        @NotBlank @URL String url
    ) {
        this.webClient = WebClient.builder().filter(ClientStatusCodeHandler.ERROR_RESPONSE_FILTER).baseUrl(url).build();
    }

    public Optional<StackOverflowResponse> fetchQuestion(Long id) {
        return webClient.get()
                        .uri(uriBuilder -> uriBuilder.path("2.3/questions/{id}")
                                                     .queryParam("site", "stackoverflow")
                                                     .queryParam("sort", "activity").build(id))
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(StackOverflowResponse.class)
                        .onErrorResume(Exception.class, e -> Mono.empty())
                        .blockOptional();
    }

}
