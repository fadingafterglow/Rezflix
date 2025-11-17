package ua.edu.ukma.springers.rezflix.aspects.limit;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    int limitPerMinute() default 100;
}