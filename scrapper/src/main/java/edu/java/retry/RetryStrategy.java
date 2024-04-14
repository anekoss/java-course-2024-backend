package edu.java.retry;

import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.util.List;


public interface RetryStrategy {

    default boolean filter(Throwable throwable, List<Integer> statusCodes) {
        return throwable instanceof WebClientResponseException && statusCodes
                .contains(((WebClientResponseException) throwable).getStatusCode()
                                                                  .value());
    }

    Retry getRetryPolice();
}
