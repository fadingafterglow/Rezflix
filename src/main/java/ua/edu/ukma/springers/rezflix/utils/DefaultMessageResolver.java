package ua.edu.ukma.springers.rezflix.utils;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(MessageSource.class)
public class DefaultMessageResolver implements MessageResolver {

    public String resolve(String message, Object... args) {
        return message;
    }
}
