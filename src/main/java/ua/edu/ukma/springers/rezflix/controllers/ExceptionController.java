package ua.edu.ukma.springers.rezflix.controllers;

import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException; // Added import
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.ErrorResponseDto;
import ua.edu.ukma.springers.rezflix.exceptions.*;
import ua.edu.ukma.springers.rezflix.logging.Markers;
import ua.edu.ukma.springers.rezflix.utils.MessageResolver;

import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

    private final MessageResolver messageResolver;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> badRequest(MethodArgumentNotValidException ex) {
        ErrorResponseDto response = responseOf("error.application.invalid-data");
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getCodes)
                .filter(codes -> codes != null && codes.length > 0)
                .map(codes -> codes[0])
                .map(messageResolver::resolve)
                .toList();
        response.setDetails(details);
        return toResponseEntity(HttpStatus.BAD_REQUEST, response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDto> badRequest(ValidationException ex) {
        ErrorResponseDto response = responseOf(ex);
        List<String> details = ex.getViolations().stream()
                .map(ConstraintViolation::getMessage)
                .map(messageResolver::resolve)
                .toList();
        response.setDetails(details);
        return toResponseEntity(HttpStatus.BAD_REQUEST, response);
    }

    @ExceptionHandler({
            HttpMessageConversionException.class,
            MissingRequestValueException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponseDto> badRequest() {
        return toResponseEntity(HttpStatus.BAD_REQUEST, responseOf("error.application.invalid-data"));
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ErrorResponseDto> unauthorized(UnauthenticatedException ex) {
        return toResponseEntity(HttpStatus.UNAUTHORIZED, responseOf(ex));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDto> forbidden(ForbiddenException ex) {
        return toResponseEntity(HttpStatus.FORBIDDEN, responseOf(ex));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDto> notFound() {
        return toResponseEntity(HttpStatus.NOT_FOUND, responseOf("error.application.no-resource"));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> notFound(NotFoundException ex) {
        ErrorResponseDto response = responseOf(ex);
        if (ex.getEntityClass() != null) {
            String detailedMessage = messageResolver.resolve(
                    "error.application.no-entity",
                    ex.getEntityClass().getSimpleName(), ex.getParameters()
            );
            response.setDetails(List.of(detailedMessage));
        }
        return toResponseEntity(HttpStatus.NOT_FOUND, response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> methodNotAllowed() {
        return toResponseEntity(HttpStatus.METHOD_NOT_ALLOWED, responseOf("error.application.method-not-allowed"));
    }

    @ExceptionHandler(AllRetryAttemptsUsedException.class)
    public ResponseEntity<ErrorResponseDto> serviceUnavailable(AllRetryAttemptsUsedException ex) {
        return toResponseEntity(HttpStatus.SERVICE_UNAVAILABLE, responseOf(ex));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponseDto> rateLimitExceeded(RateLimitExceededException ex) {
        return toResponseEntity(HttpStatus.TOO_MANY_REQUESTS, responseOf(ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> internalServerError(Exception ex) {
        log.error(Markers.EXCEPTION, "Unexpected exception", ex);
        return toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, responseOf("error.application.unexpected"));
    }

    private ResponseEntity<ErrorResponseDto> toResponseEntity(HttpStatusCode status, ErrorResponseDto body) {
        log.warn(Markers.EXCEPTION, "Exception caused response with status {} and body {}", status.value(), body);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    private ErrorResponseDto responseOf(BaseException exception) {
        return responseOf(exception.getMessage());
    }

    private ErrorResponseDto responseOf(String message) {
        return new ErrorResponseDto(messageResolver.resolve(message), List.of());
    }
}