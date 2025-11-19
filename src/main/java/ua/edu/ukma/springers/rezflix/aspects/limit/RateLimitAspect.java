package ua.edu.ukma.springers.rezflix.aspects.limit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.exceptions.RateLimitExceededException;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final Map<String, Map<String, Queue<Long>>> calls = new ConcurrentHashMap<>();

    private final SecurityUtils securityUtils;
    private final HttpServletRequest request;

    @Pointcut("execution(* *(..)) && @annotation(rateLimited)")
    public void rateLimitedMethod(RateLimited rateLimited) {}

    @Before("rateLimitedMethod(rateLimited)")
    public void enforceLimit(JoinPoint jp, RateLimited rateLimited) {
        String clientKey = clientKey();
        String methodKey = jp.getSignature().toLongString();

        int limit = rateLimited.limitPerMinute();
        long now = System.currentTimeMillis();
        long oneMinuteAgo = now - 60_000;

        Map<String, Queue<Long>> clientMap =
                calls.computeIfAbsent(clientKey, key -> new ConcurrentHashMap<>());

        Queue<Long> timestamps =
                clientMap.computeIfAbsent(methodKey, key -> new LinkedList<>());

        synchronized (timestamps) {
            while (true) {
                Long head = timestamps.peek();
                if (head == null || head >= oneMinuteAgo) break;
                timestamps.poll();
            }

            if (timestamps.size() >= limit) {
                log.warn("Rate limit {} exceeded for method: {} by client: {}", limit, methodKey, clientKey);
                throw new RateLimitExceededException();
            }

            timestamps.add(now);
        }
    }

    private String clientKey() {
        Integer userId = securityUtils.getCurrentUserId();
        if (userId != null) {
            return "USER_" + userId;
        } else {
            return "ANONYMOUS_" + clientIp();
        }
    }

    private String clientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip)) return ip.split(",")[0];
        return request.getRemoteAddr();
    }
}
