package edu.java.client;

import edu.java.response.StackOverflowResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class StackOverflowClient {
    private final String defaultUrl = "https://api.stackexchange.com";
    private final WebClient webClient;

    public StackOverflowClient(String url) {
        this.webClient = WebClient.builder().baseUrl(url).build();
    }

    public StackOverflowClient() {
        this.webClient = WebClient.builder().baseUrl(defaultUrl).build();
    }

    Mono<StackOverflowResponse> fetchQuestion(Long id) {
        return webClient.get().uri("answers/{ids}/questions", id).retrieve().bodyToMono(StackOverflowResponse.class)
            .doOnError(error -> log.error(error.getMessage()));
    }

}
