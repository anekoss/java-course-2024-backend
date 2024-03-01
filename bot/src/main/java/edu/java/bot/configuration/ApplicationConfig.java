package edu.java.bot.configuration;

import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.validation.constraints.NotEmpty;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = true)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken
) {

    @Bean
    public SetMyCommands setMyCommandAs() {
        return new SetMyCommands();
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(1);
    }

}
