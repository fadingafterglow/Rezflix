package ua.edu.ukma.springers.rezflix.validators;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.utils.SecurityUtils;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public abstract class BaseValidator<E> implements IValidator<E> {

    protected final Validator validator;
    protected final SecurityUtils securityUtils;

    @Override
    public void validForView(E entity) {}

    @Override
    public void validForView(List<E> entities) {
        for (E entity : entities)
            validForView(entity);
    }

    @Override
    public void validForCreate(E entity) {
        securityUtils.authenticated();
        validateData(entity);
    }

    @Override
    public void validForUpdate(E entity) {
        securityUtils.authenticated();
        validateData(entity);
    }

    @Override
    public void validForDelete(E entity) {
        securityUtils.authenticated();
    }

    protected void validateData(E entity) {
        Set<ConstraintViolation<E>> violations = validator.validate(entity);
        if (violations != null && !violations.isEmpty())
            throw new ValidationException(violations);
    }
}