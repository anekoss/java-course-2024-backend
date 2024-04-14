package edu.java.bot.configuration.retry;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.retry.strategy.ExponentialRetryStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.retry.Retry;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "retry-config.policy", havingValue = "exponential")
public class ExponentialRetryConfig {
    @Bean
    public Retry retry(ApplicationConfig.RetryConfig retryConfig) {
        return new ExponentialRetryStrategy(
            retryConfig.maxAttempts(),
            retryConfig.backoff(),
            retryConfig.maxBackoff(),
            retryConfig.jitter(),
            retryConfig.statusCodes()
        ).getRetryPolice();
    }
}
