package ua.edu.ukma.springers.rezflix.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import ua.edu.ukma.springers.rezflix.exceptions.UnauthenticatedException;

@Component
@RequiredArgsConstructor
public class WatchRoomExceptionHandler extends StompSubProtocolErrorHandler {

    private final MessageResolver messageResolver;

    @NonNull
    @Override
    protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor, @NonNull byte[] errorPayload, Throwable cause, StompHeaderAccessor clientHeaderAccessor) {
        errorHeaderAccessor.setMessage(resolveMessage(cause));
        return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
    }

    private String resolveMessage(Throwable ex) {
        if (ex instanceof MessageDeliveryException)
            ex = ex.getCause();
        String message;
        if (ex instanceof UnauthenticatedException e)
            message = e.getMessage();
        else if (ex instanceof AccessDeniedException)
            message = "error.application.forbidden";
        else
            message = "error.application.unexpected";
        return messageResolver.resolve(message);
    }
}
