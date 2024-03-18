package edu.java.client;

import edu.java.client.dto.StackOverflowResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public class StackOverflowClient {
    private final WebClient webClient;

    public StackOverflowClient(@Value("${app.client.stackOverflow.base-url}") @NotBlank @URL String url) {
        this.webClient = WebClient.builder().baseUrl(url).build();
    }

    public StackOverflowResponse fetchQuestion(Integer id) {
        return webClient.get()
                        .uri(uriBuilder -> uriBuilder.path("2.3/questions/{id}").queryParam("site", "stackoverflow")
                                                     .queryParam("sort", "activity").build(id))
                        .retrieve()
                        .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                            throw new HttpServerErrorException(clientResponse.statusCode());
                        })
                        .bodyToMono(StackOverflowResponse.class)
                        .onErrorMap(error -> {
                            throw new IllegalArgumentException(error.getMessage());
                        })
                        .block();
    }

}
