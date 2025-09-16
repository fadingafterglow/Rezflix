package ua.edu.ukma.springers.rezflix.exceptions;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Set;

@Getter
public class ValidationException extends BaseException {

    private Set<? extends ConstraintViolation<?>> violations = Set.of();

    public ValidationException() {
        super("error.application.invalid-data");
    }

    public ValidationException(String message) {
        super(message);
    }

    public <T> ValidationException(Set<ConstraintViolation<T>> violations) {
        this();
        this.violations = violations;
    }
}
