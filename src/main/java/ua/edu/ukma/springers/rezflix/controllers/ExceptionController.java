package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.ErrorResponseDto;
import ua.edu.ukma.springers.rezflix.exceptions.*;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

    private final ExceptionTranslator exceptionTranslator;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> internalServerError(Exception ex) {
        log.error("Unexpected exception", ex);
        return toResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> methodNotAllowed(Exception ex) {
        return toResponse(HttpStatus.METHOD_NOT_ALLOWED, ex);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDto> forbidden(Exception ex) {
        return toResponse(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler({
        UnauthenticatedException.class,
        AuthenticationException.class
    })
    public ResponseEntity<ErrorResponseDto> unauthorized(Exception ex) {
        return toResponse(HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler({
        NoResourceFoundException.class,
        NotFoundException.class
    })
    public ResponseEntity<ErrorResponseDto> notFound(Exception ex) {
        return toResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        ValidationException.class
    })
    public ResponseEntity<ErrorResponseDto> badRequest(Exception ex) {
        return toResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(ConcurrencyFailureException.class)
    public ResponseEntity<ErrorResponseDto> conflict(Exception ex) {
        return toResponse(HttpStatus.CONFLICT, ex);
    }

    private <T extends Throwable> ResponseEntity<ErrorResponseDto> toResponse(HttpStatusCode code, T exception) {
        return ResponseEntity.status(code)
                .contentType(MediaType.APPLICATION_JSON)
                .body(exceptionTranslator.translate(exception));
    }
}
