package ua.edu.ukma.springers.rezflix.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.ErrorResponseDto;

import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ExceptionTranslator {
    
    private final MessageSource messageSource;
    private final TranslatorsMap translators = new TranslatorsMap(t -> toBaseResponse("error.application.unexpected"))
        .add(NoResourceFoundException.class, t -> toBaseResponse("error.application.no-resource"))
        .add(HttpRequestMethodNotSupportedException.class, t -> toBaseResponse("error.application.method-not-allowed"))
        .add(MethodArgumentNotValidException.class, t -> {
            ErrorResponseDto errorResponseDto = toBaseResponse("error.application.invalid-data");
            List<String> details = t.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getCodes)
                    .filter(codes -> codes != null && codes.length > 0)
                    .map(codes -> codes[0])
                    .map(this::translateMessage)
                    .toList();
            errorResponseDto.setDetails(details);
            return errorResponseDto;
        })
        .add(ForbiddenException.class, this::toBaseResponse)
        .add(UnauthenticatedException.class, this::toBaseResponse)
        .add(AuthenticationException.class, t -> toBaseResponse("error.application.unauthenticated"))
        .add(ConcurrencyFailureException.class, t -> toBaseResponse("error.application.retry-exhausted"))
        .add(NotFoundException.class, t -> {
            ErrorResponseDto errorResponseDto = toBaseResponse(t);
            if (t.getEntityClass() != null) {
                String message = translateMessage("error.application.no-entity", t.getEntityClass().getSimpleName(), t.getParameters());
                errorResponseDto.setDetails(List.of(message));
            }
            return errorResponseDto;
        })
        .add(ValidationException.class, t -> {
            ErrorResponseDto errorResponseDto = toBaseResponse(t);
            List<String> details = t.getViolations().stream()
                    .map(v -> translateMessage(v.getMessage()))
                    .toList();
            errorResponseDto.setDetails(details);
            return errorResponseDto;
        });

    @SuppressWarnings("unchecked")
    public <T extends Throwable> ErrorResponseDto translate(T exception) {
        return translators.get((Class<T>) exception.getClass()).apply(exception);
    }

    private ErrorResponseDto toBaseResponse(Throwable exception) {
        return toBaseResponse(exception.getMessage());
    }

    private ErrorResponseDto toBaseResponse(String message) {
        return new ErrorResponseDto(translateMessage(message), new ArrayList<>());
    }

    private String translateMessage(String message, Object... args) {
        LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
        Locale locale = localeContext == null ? null : localeContext.getLocale();
        return messageSource.getMessage(message, args, locale == null ? Locale.ROOT : locale);
    }

    private static class TranslatorsMap {

        private final Map<Class<? extends Throwable>, Function<?, ErrorResponseDto>> map = new HashMap<>();
        private final Function<Throwable, ErrorResponseDto> defaultTranslator;

        public TranslatorsMap(Function<Throwable, ErrorResponseDto> defaultTranslator) {
            this.defaultTranslator = defaultTranslator;
        }

        public <T extends Throwable> TranslatorsMap add(Class<T> exceptionClass, Function<T, ErrorResponseDto> translator) {
            map.put(exceptionClass, translator);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T extends Throwable> Function<T, ErrorResponseDto> get(Class<T> exceptionClass) {
            Class<?> keyClass = exceptionClass;
            Object translator;
            while (true) {
                translator = map.get(keyClass);
                if (translator != null)
                    break;
                keyClass = keyClass.getSuperclass();
                if (keyClass == Throwable.class) {
                    translator = defaultTranslator;
                    break;
                }
            }
            return (Function<T, ErrorResponseDto>) translator;
        }
    }
}
