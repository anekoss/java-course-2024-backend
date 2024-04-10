package edu.java.bot.client;

import edu.java.bot.client.exception.CustomClientErrorException;
import edu.java.bot.client.exception.CustomServerErrorException;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.codec.CodecException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static edu.java.bot.client.ClientStatusCodeHandler.ERROR_RESPONSE_FILTER;

@Slf4j
@Component
public class TgChatClient {

    private final String pathId = "/{id}";
    private final WebClient webCLient;

    public TgChatClient(
        @Value("${app.client.tg-сhat.base-url}")
        @NotBlank @URL String url
    ) {
        this.webCLient = WebClient.builder().filter(ERROR_RESPONSE_FILTER).baseUrl(url).build();
    }

    public Void registerChat(Long id) throws CustomClientErrorException, CustomServerErrorException {
        try {

            return webCLient.post()
                            .uri(pathId, id)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .block();
        } catch (WebClientResponseException | CodecException e) {
            log.error(e.getMessage());
            throw new CustomClientErrorException();
        } catch (Exception e) {
            throw new CustomServerErrorException();
        }
    }

    public Void deleteChat(Long id) throws CustomClientErrorException, CustomServerErrorException {
        try {
            return webCLient.delete()
                            .uri(pathId, id)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .block();
        } catch (WebClientResponseException | CodecException e) {
            log.error(e.getMessage());
            throw new CustomClientErrorException();
        } catch (Exception e) {
            throw new CustomServerErrorException();
        }
    }

}
