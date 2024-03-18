package edu.java.bot.client;

import edu.java.bot.client.dto.AddLinkRequest;
import edu.java.bot.client.dto.LinkResponse;
import edu.java.bot.client.dto.ListLinksResponse;
import edu.java.bot.client.dto.RemoveLinkRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class LinksClient {
    private final String tgChatIdHeader = "Tg-Chat-Id";
    private final WebClient webCLient;

    public LinksClient(@Value("${app.client.linksClient.base-url}") @NotBlank @URL String url) {
        this.webCLient = WebClient.builder().baseUrl(url).build();
    }

    public ListLinksResponse getLinks(Long tgChatId) {
        return webCLient.get()
                        .header(tgChatIdHeader, String.valueOf(tgChatId))
                        .retrieve()
                        .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                            throw new HttpServerErrorException(clientResponse.statusCode());
                        })
                        .bodyToMono(ListLinksResponse.class)
                        .onErrorMap(error -> {
                            throw new IllegalArgumentException(error.getMessage());
                        })
                        .block();
    }

    public LinkResponse deleteLink(Long tgChatId, RemoveLinkRequest request) {
        return webCLient.method(HttpMethod.DELETE)
                        .header(tgChatIdHeader, String.valueOf(tgChatId))
                        .bodyValue(request)
                        .retrieve()
                        .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                            throw new HttpServerErrorException(clientResponse.statusCode());
                        })
                        .bodyToMono(LinkResponse.class)
                        .onErrorMap(error -> {
                            throw new IllegalArgumentException(error.getMessage());
                        })
                        .block();
    }

    public LinkResponse addLink(Long tgChatId, AddLinkRequest request) {
        return webCLient.post()
                        .header(tgChatIdHeader, String.valueOf(tgChatId))
                        .bodyValue(request)
                        .retrieve()
                        .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                            throw new HttpServerErrorException(clientResponse.statusCode());
                        })
                        .bodyToMono(LinkResponse.class)
                        .onErrorMap(error -> {
                            throw new IllegalArgumentException(error.getMessage());
                        })
                        .block();
    }
}


