package ua.edu.ukma.springers.rezflix.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MessageResolver {

    private final MessageSource messageSource;

    public String resolve(String message, Object... args) {
        LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
        Locale locale = localeContext == null ? null : localeContext.getLocale();
        return messageSource.getMessage(message, args, locale == null ? Locale.ROOT : locale);
    }
}
