package edu.java.bot.client;

import edu.java.bot.client.dto.AddLinkRequest;
import edu.java.bot.client.dto.LinkResponse;
import edu.java.bot.client.dto.ListLinksResponse;
import edu.java.bot.client.dto.RemoveLinkRequest;
import edu.java.bot.client.exception.CustomClientErrorException;
import edu.java.bot.client.exception.CustomServerErrorException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.codec.CodecException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

@Slf4j
@Component
public class LinksClient {
    private final String tgChatIdHeader = "Tg-Chat-Id";
    private final WebClient webCLient;
    private final Retry retry;

    public LinksClient(
        @Value("${app.client.links.base-url}")
        @NotBlank @URL String url,
        @NotNull Retry retry
    ) {
        this.webCLient = WebClient.builder().baseUrl(url).build();
        this.retry = retry;
    }

    public ListLinksResponse getLinks(Long tgChatId) throws CustomClientErrorException, CustomServerErrorException {
        try {
            return webCLient.get()
                .accept(MediaType.APPLICATION_JSON)
                .header(tgChatIdHeader, String.valueOf(tgChatId))
                .retrieve()
                .bodyToMono(ListLinksResponse.class)
                .retryWhen(retry)
                .block();
        } catch (WebClientResponseException | CodecException e) {
            log.error(e.getMessage());
            throw new CustomClientErrorException();
        } catch (Exception e) {
            throw new CustomServerErrorException();
        }
    }

    public LinkResponse deleteLink(Long tgChatId, RemoveLinkRequest request)
        throws CustomClientErrorException, CustomServerErrorException {
        try {
            return webCLient.method(HttpMethod.DELETE)
                .accept(MediaType.APPLICATION_JSON)
                .header(tgChatIdHeader, String.valueOf(tgChatId))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LinkResponse.class)
                .retryWhen(retry)
                .block();
        } catch (WebClientException | CodecException e) {
            log.error(e.getMessage());
            throw new CustomClientErrorException();
        } catch (Exception e) {
            throw new CustomServerErrorException();
        }
    }

    public LinkResponse addLink(Long tgChatId, AddLinkRequest request)
        throws CustomClientErrorException, CustomServerErrorException {
        try {
            return webCLient.post()
                .accept(MediaType.APPLICATION_JSON)
                .header(tgChatIdHeader, String.valueOf(tgChatId))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LinkResponse.class)
                .retryWhen(retry)
                .block();
        } catch (WebClientResponseException | CodecException e) {
            log.error(e.getMessage());
            throw new CustomClientErrorException();
        } catch (Exception e) {
            throw new CustomServerErrorException();
        }
    }
}


