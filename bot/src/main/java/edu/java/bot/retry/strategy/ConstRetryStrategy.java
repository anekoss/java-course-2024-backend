package edu.java.bot.retry.strategy;

import edu.java.bot.retry.RetryStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ConstRetryStrategy implements RetryStrategy {
    private final int maxAttempts;
    private final Duration backoff;
    private final List<Integer> statusCodes;

    @Override
    public Retry getRetryPolice() {
        return Retry.fixedDelay(maxAttempts, backoff)
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
