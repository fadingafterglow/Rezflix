package ua.edu.ukma.springers.rezflix.exceptions;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Set;

@Getter
public class ValidationException extends BaseException {

    private final transient Set<? extends ConstraintViolation<?>> violations;

    public ValidationException() {
        super("error.application.invalid-data");
        violations = Set.of();
    }

    public ValidationException(String message) {
        super(message);
        violations = Set.of();
    }

    public <T> ValidationException(Set<ConstraintViolation<T>> violations) {
        super("error.application.invalid-data");
        this.violations = violations;
    }
}
