package edu.java.rateLimiting;

import edu.java.configuration.ApplicationConfig;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateLimitingService {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final ApplicationConfig.RateLimitingConfig rateLimitingConfig;

    public Bucket resolveBucket(String ip) {
        return cache.computeIfAbsent(ip, this::newBucket);
    }

    public Bucket newBucket(@NotBlank String api) {
        Bandwidth limit = Bandwidth.classic(
            rateLimitingConfig.limit(),
            Refill.greedy(
                rateLimitingConfig.timeDuration(),
                rateLimitingConfig.nanoInSeconds()
            )
        );
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    public void clearBucket() {
        cache.clear();
    }
}
