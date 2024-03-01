package edu.java.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class TgChatClient {

    private final String defaultUrl = "http://localhost:8080/TgChatClient";
    private final String pathId = "/{id}";
    private final WebClient webCLient;

    public TgChatClient(String url) {
        this.webCLient = WebClient.builder().baseUrl(url).build();
    }

    public TgChatClient() {
        this.webCLient = WebClient.builder().baseUrl(defaultUrl).build();
    }

    public ResponseEntity<Void> registerChat(Long id) {
        return webCLient.post().uri(pathId, id).retrieve().toEntity(Void.class)
            .doOnError(error -> log.error(error.getMessage())).block();
    }

    public ResponseEntity<Void> deleteChat(Long id) {
        return webCLient.delete().uri(pathId, id).retrieve().toEntity(Void.class)
            .doOnError(error -> log.error(error.getMessage())).block();
    }

}
