package edu.java.rateLimiting;

import edu.java.controller.exception.RateLimitingException;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitingInterceptor implements HandlerInterceptor {
    private final RateLimitingService rateLimitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        String ip = request.getRemoteAddr();
        Bucket bucket = rateLimitService.resolveBucket(ip);
        if (bucket.tryConsume(1)) {
            return true;
        }
        throw new RateLimitingException();
    }

}
