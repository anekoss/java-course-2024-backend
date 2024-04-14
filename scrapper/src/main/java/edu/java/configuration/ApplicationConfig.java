package edu.java.configuration;

import edu.java.retry.RetryPolicy;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = true)
public record ApplicationConfig(@NotNull @Bean Scheduler scheduler, @NotNull @Bean RetryConfig retryConfig,
                                @NotNull @Bean RateLimitingConfig rateLimitingConfig) {
    public record Scheduler(boolean enable,
                            @NotNull Duration interval,
                            @NotNull Duration forceCheckDelay,
                            @NotNull Long limit) {
    }

    public record RetryConfig(@NotNull RetryPolicy policy,
                              @Positive int maxAttempts,
                              @NotNull Duration backoff,
                              Duration maxBackoff,
                              @NotNull List<Integer> statusCodes,
                              @Min(0) @Max(1) double jitter) {

    }

    public record RateLimitingConfig(@Positive int limit,
                                     @Positive int timeDuration,
                                     @NotNull Duration nanoInSeconds) {

    }

}
