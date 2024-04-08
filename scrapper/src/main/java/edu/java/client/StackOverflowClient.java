package edu.java.client;

import edu.java.client.dto.StackOverflowResponse;
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

@Slf4j
@Component
public class StackOverflowClient {
    private final WebClient webClient;

    public StackOverflowClient(
            @Value("${app.client.stackOverflow.base-url}")
            @NotBlank @URL String url
    ) {
        this.webClient = WebClient.builder().baseUrl(url).build();
    }

    public StackOverflowResponse fetchQuestion(Long id) throws CustomWebClientException {
        try {
            return webClient.get()
                            .uri(uriBuilder -> uriBuilder.path("2.3/questions/{id}")
                                                         .queryParam("site", "stackoverflow")
                                                         .queryParam("sort", "activity").build(id))
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(StackOverflowResponse.class)
                            .block();
        } catch (WebClientRequestException | WebClientResponseException | CodecException e) {
            log.warn(e.getMessage());
            throw new CustomWebClientException();
        }
    }

}
