package ua.edu.ukma.springers.rezflix.validators;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;

import java.util.List;
import java.util.Set;

public abstract class BaseValidator<E> implements IValidator<E> {

    @Setter(onMethod_ =  @Autowired)
    protected Validator validator;

    @Override
    public void validForView(E entity) {}

    @Override
    public void validForView(List<E> entities) {
        for (E entity : entities)
            validForView(entity);
    }

    @Override
    public void validForCreate(E entity) {
        validateData(entity);
    }

    @Override
    public void validForUpdate(E entity) {
        validateData(entity);
    }

    @Override
    public void validForDelete(E entity) {}

    protected void validateData(E entity) {
        Set<ConstraintViolation<E>> violations = validator.validate(entity);
        if (violations != null && !violations.isEmpty())
            throw new ValidationException(violations);
    }
}