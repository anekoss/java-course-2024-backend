package edu.java.bot.client;

import edu.java.bot.client.dto.LinkUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class BotClient {

    private final String defaultUrl = "http://localhost:8090";
    private final WebClient webCLient;

    public BotClient(String url) {
        this.webCLient = WebClient.builder().baseUrl(url).build();
    }

    public BotClient() {
        this.webCLient = WebClient.builder().baseUrl(defaultUrl).build();
    }

    public ResponseEntity<Void> linkUpdates(LinkUpdateRequest request) {
        return webCLient.post().uri("/updates").body(request, LinkUpdateRequest.class).retrieve().toEntity(Void.class)
            .doOnError(error -> log.error(error.getMessage())).block();
    }
}
