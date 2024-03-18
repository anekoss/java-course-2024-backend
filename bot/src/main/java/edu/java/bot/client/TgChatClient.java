package edu.java.bot.client;

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
public class TgChatClient {

    private final String pathId = "/{id}";
    private final WebClient webCLient;

    public TgChatClient(@Value("${app.client.tgChatClient.base-url}") @NotBlank @URL String url) {
        this.webCLient = WebClient.builder().baseUrl(url).build();
    }

    public Void registerChat(Long id) {
        return webCLient.post().uri(pathId, id).retrieve()
                        .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                            throw new HttpServerErrorException(clientResponse.statusCode());
                        })
                        .bodyToMono(Void.class)
                        .onErrorMap(error -> {
                            throw new IllegalArgumentException(error.getMessage());
                        })
                        .block();
    }

    public Void deleteChat(Long id) {
        return webCLient.delete().uri(pathId, id).retrieve()
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
