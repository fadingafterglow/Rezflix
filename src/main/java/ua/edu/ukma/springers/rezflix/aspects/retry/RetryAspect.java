package ua.edu.ukma.springers.rezflix.aspects.retry;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.exceptions.AllRetryAttemptsUsedException;

@Slf4j
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class RetryAspect {

    @Pointcut("execution(* *(..)) && @annotation(retryable)")
    public void retryableMethod(Retryable retryable) {}

    @Around("retryableMethod(retryable)")
    public Object retryIfRequired(ProceedingJoinPoint joinPoint, Retryable retryable) throws Throwable {
        int attempts = retryable.attempts();
        while (attempts > 0) {
            try {
                return joinPoint.proceed();
            } catch (Throwable ex) {
                if (!shouldRetryFor(ex, retryable))
                    throw ex;
                if (--attempts == 0)
                    break;
                log.warn("Retrying: {}; attempts left: {}", joinPoint.getSignature().toShortString(), attempts);
                Thread.sleep(retryable.delayMs());
            }
        }
        throw new AllRetryAttemptsUsedException();
    }

    private boolean shouldRetryFor(Throwable ex, Retryable retryable) {
        boolean shouldRetry = retryable.retryFor().length == 0;
        for (Class<? extends Throwable> retryableException : retryable.retryFor()) {
            if (retryableException.isInstance(ex)) {
                shouldRetry = true;
                break;
            }
        }
        return shouldRetry;
    }
}
