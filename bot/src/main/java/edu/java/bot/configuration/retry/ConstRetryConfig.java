package edu.java.bot.configuration.retry;


import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.retry.strategy.ConstRetryStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.retry.Retry;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "retry-config.policy", havingValue = "constant")
public class ConstRetryConfig {

    @Bean
    public Retry retry(ApplicationConfig.RetryConfig retryConfig) {
        return new ConstRetryStrategy(retryConfig.maxAttempts(), retryConfig.backoff(), retryConfig.statusCodes()).getRetryPolice();
    }
}
