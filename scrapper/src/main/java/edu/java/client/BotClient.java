package edu.java.client;

import edu.java.client.dto.LinkUpdateRequest;
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
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BotClient {

    private final WebClient webCLient;

    public BotClient(
            @Value("${app.client.botClient.base-url}")
            @NotBlank @URL String url
    ) {
        this.webCLient = WebClient.builder().baseUrl(url).build();
    }

    public String linkUpdates(LinkUpdateRequest request) throws CustomWebClientException {
        try {
            return webCLient
                    .post()
                    .uri("/updates")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.just(request), LinkUpdateRequest.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientRequestException | WebClientResponseException | CodecException e) {
            log.warn(e.getMessage());
            throw new CustomWebClientException();
        }
    }
}
