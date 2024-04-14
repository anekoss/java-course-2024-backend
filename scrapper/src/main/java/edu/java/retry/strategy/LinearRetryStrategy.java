package edu.java.retry.strategy;

import edu.java.retry.RetryStrategy;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

@Slf4j
@RequiredArgsConstructor
public class LinearRetryStrategy implements RetryStrategy {
    private final int maxAttempts;
    private final Duration backoff;
    private final Duration maxBackoff;
    private final List<Integer> statusCodes;

    @Override
    public Retry getRetryPolice() {
        return Retry.fixedDelay(maxAttempts, backoff)
            .maxBackoff(maxBackoff)
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
