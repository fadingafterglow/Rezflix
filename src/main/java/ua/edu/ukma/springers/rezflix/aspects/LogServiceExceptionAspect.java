package ua.edu.ukma.springers.rezflix.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogServiceExceptionAspect {
    @Pointcut("within(ua.edu.ukma.springers.rezflix.services..*)")
    public void serviceMethod() {}

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {}

    @AfterThrowing(pointcut = "serviceMethod() && publicMethod()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        log.error("Exception in {}: {}", joinPoint.getSignature().toShortString(), ex.getMessage());
    }
}
