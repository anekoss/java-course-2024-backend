package edu.java.bot.retry.strategy;

import edu.java.bot.retry.RetryStrategy;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

@Slf4j
@RequiredArgsConstructor
public class ExponentialRetryStrategy implements RetryStrategy {
    private final int maxAttempts;
    private final Duration backoff;
    private final Duration maxBackoff;
    private final double jitter;
    private final List<Integer> statusCodes;

    @Override
    public Retry getRetryPolice() {
        return Retry.backoff(maxAttempts, backoff)
            .maxBackoff(maxBackoff)
            .jitter(jitter)
            .filter(throwable -> filter(throwable, statusCodes))
            .doBeforeRetry(retrySignal -> {
                log.warn(
                    "Retry attempt {} due to response with code {}",
                    retrySignal.totalRetries(),
                    ((WebClientResponseException) retrySignal.failure()).getStatusCode()
                );
            });
    }
}
