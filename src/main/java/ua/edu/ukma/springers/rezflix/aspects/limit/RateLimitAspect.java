package ua.edu.ukma.springers.rezflix.aspects.limit;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.data.redis.core.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.exceptions.RateLimitExceededException;

import java.time.Duration;

@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    private final RedisTemplate<String, String> redisTemplate;

    public RateLimitAspect(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(rateLimited)")
    public Object enforceLimit(ProceedingJoinPoint pjp, RateLimited rateLimited) throws Throwable {

        String userId = getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("User ID is null - cannot apply rate limiting");
        }

        String methodKey = pjp.getSignature().toLongString();
        int limit = rateLimited.limitPerMinute();

        long now = System.currentTimeMillis();
        long oneMinuteAgo = now - 60_000;

        String redisKey = "rate-limit:" + userId + ":" + methodKey;

        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();

        // Remove requests older than 1 minute
        zset.removeRangeByScore(redisKey, 0, oneMinuteAgo);

        Long count = zset.count(redisKey, oneMinuteAgo, now);
        if (count == null) count = 0L;

        if (count >= limit) {
            throw new RateLimitExceededException();
        }

        zset.add(redisKey, String.valueOf(now), now);

        // clear redis
        redisTemplate.expire(redisKey, Duration.ofMinutes(2));

        return pjp.proceed();
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        return auth.getName();
    }
}