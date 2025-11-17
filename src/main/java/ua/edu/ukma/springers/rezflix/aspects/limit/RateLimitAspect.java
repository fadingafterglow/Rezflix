package ua.edu.ukma.springers.rezflix.aspects.limit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.exceptions.RateLimitExceededException;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Aspect
@Component
public class RateLimitAspect {

    private final Map<String, Map<String, Queue<Long>>> calls = new ConcurrentHashMap<>();

    private final HttpServletRequest request;

    public RateLimitAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Pointcut("@annotation(rateLimited)")
    public void annotatedWithRateLimit(RateLimited rateLimited) {
    }

    @Before("annotatedWithRateLimit(rateLimited)")
    public void enforceLimit(JoinPoint jp, RateLimited rateLimited) {
        String clientKey = getClientIp();
        String methodKey = jp.getSignature().toLongString();

        int limit = rateLimited.limitPerMinute();
        long now = System.currentTimeMillis();
        long oneMinuteAgo = now - 60_000;

        Map<String, Queue<Long>> clientMap =
                calls.computeIfAbsent(clientKey, key -> new ConcurrentHashMap<>());

        Queue<Long> timestamps =
                clientMap.computeIfAbsent(methodKey, key -> new ConcurrentLinkedQueue<>());

        while (true) {
            Long head = timestamps.peek();
            if (head == null || head >= oneMinuteAgo) break;
            timestamps.poll();
        }

        if (timestamps.size() >= limit) {
            throw new RateLimitExceededException();
        }

        timestamps.add(now);
    }

    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) return ip.split(",")[0];
        return request.getRemoteAddr();
    }
}
