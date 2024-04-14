package edu.java.client;

import edu.java.client.exception.CustomWebClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Slf4j
public class ClientStatusCodeHandler {
    public static final ExchangeFilterFunction ERROR_RESPONSE_FILTER = ExchangeFilterFunction
        .ofResponseProcessor(ClientStatusCodeHandler::exchangeFilterResponseProcessor);

    private ClientStatusCodeHandler() {

    }

    private static Mono<ClientResponse> exchangeFilterResponseProcessor(ClientResponse response) {
        if (response.statusCode().is5xxServerError()) {
            log.error("SERVER_ERROR {}", response.statusCode());
            return Mono.error(new CustomWebClientException());
        }
        if (response.statusCode().is4xxClientError()) {
            log.error("CLIENT_ERROR {}", response.statusCode());
            return Mono.error(new CustomWebClientException());
        }
        return Mono.just(response);
    }
}
