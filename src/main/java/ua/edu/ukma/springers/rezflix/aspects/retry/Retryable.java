package ua.edu.ukma.springers.rezflix.aspects.retry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retryable {
    int attempts() default 3;
    long delayMs() default 100;
    Class<? extends Throwable>[] retryFor() default {};
}
