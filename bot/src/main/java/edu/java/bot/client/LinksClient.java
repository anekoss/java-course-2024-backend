package edu.java.bot.client;

import edu.java.controller.dto.AddLinkRequest;
import edu.java.controller.dto.LinkResponse;
import edu.java.controller.dto.ListLinksResponse;
import edu.java.controller.dto.RemoveLinkRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class LinksClient {

    private final String defaultUrl = "http://localhost:8080/links";
    private final String tgChatIdHeader = "Tg-Chat-Id";
    private final WebClient webCLient;

    public LinksClient(String url) {
        this.webCLient = WebClient.builder().baseUrl(url).build();
    }

    public LinksClient() {
        this.webCLient = WebClient.builder().baseUrl(defaultUrl).build();
    }

    public ResponseEntity<ListLinksResponse> getLinks(Long tgChatId) {
        return webCLient.get().header(tgChatIdHeader, String.valueOf(tgChatId)).retrieve()
            .toEntity(ListLinksResponse.class)
            .doOnError(error -> log.error(error.getMessage())).block();
    }

    public ResponseEntity<LinkResponse> deleteLink(Long tgChatId, RemoveLinkRequest request) {
        return webCLient.method(HttpMethod.DELETE).header(tgChatIdHeader, String.valueOf(tgChatId)).bodyValue(request)
            .retrieve()
            .toEntity(LinkResponse.class).doOnError(error -> log.error(error.getMessage())).block();
    }

    public ResponseEntity<LinkResponse> addLink(Long tgChatId, AddLinkRequest request) {
        return webCLient.post().header(tgChatIdHeader, String.valueOf(tgChatId)).bodyValue(request).retrieve()
            .toEntity(LinkResponse.class).doOnError(error -> log.error(error.getMessage())).block();
    }

    public ResponseEntity<LinkResponse> updateLink(Long tgChatId, AddLinkRequest request) {
        return webCLient.
    }

}


