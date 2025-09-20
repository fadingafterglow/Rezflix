package ua.edu.ukma.springers.rezflix.validators;

import java.util.List;

public interface IValidator<E> {

    void validForView(E entity);

    void validForView(List<E> entity);

    void validForCreate(E entity);

    void validForUpdate(E entity);

    void validForDelete(E entity);
}