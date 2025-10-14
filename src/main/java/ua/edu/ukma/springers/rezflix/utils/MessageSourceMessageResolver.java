package ua.edu.ukma.springers.rezflix.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@ConditionalOnBean(MessageSource.class)
@RequiredArgsConstructor
public class MessageSourceMessageResolver implements MessageResolver {

    private final MessageSource messageSource;

    public String resolve(String message, Object... args) {
        LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
        Locale locale = localeContext == null ? null : localeContext.getLocale();
        return messageSource.getMessage(message, args, locale == null ? Locale.ROOT : locale);
    }
}
