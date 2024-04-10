package edu.java.bot.client;

import edu.java.bot.client.exception.CustomClientErrorException;
import edu.java.bot.client.exception.CustomServerErrorException;
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
            return Mono.error(new CustomServerErrorException());
        }
        if (response.statusCode().is4xxClientError()) {
            log.error("CLIENT_ERROR {}", response.statusCode());
            return Mono.error(new CustomClientErrorException());
        }
        return Mono.just(response);
    }
}
