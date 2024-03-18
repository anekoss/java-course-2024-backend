package edu.java.client;

import edu.java.client.dto.LinkUpdateRequest;
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
public class BotClient {

    private final WebClient webCLient;

    public BotClient(@Value("${app.client.botClient.base-url}") @NotBlank @URL String url) {
        this.webCLient = WebClient.builder().baseUrl(url).build();
    }

    public Void linkUpdates(LinkUpdateRequest request) {
        return webCLient
            .post()
            .uri("/updates")
            .body(request, LinkUpdateRequest.class)
            .retrieve()
            .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                throw new HttpServerErrorException(clientResponse.statusCode());
            })
            .bodyToMono(Void.class)
            .onErrorMap(error -> {
                throw new IllegalArgumentException(error.getMessage());
            })
            .block();
    }
}
