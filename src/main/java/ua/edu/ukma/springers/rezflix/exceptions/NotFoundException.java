package ua.edu.ukma.springers.rezflix.exceptions;

import lombok.Getter;

@Getter
public class NotFoundException extends BaseException {

    private final Class<?> entityClass;
    private final String parameters;

    public NotFoundException() {
        super("error.application.no-resource");
        this.entityClass = null;
        this.parameters = null;
    }

    public NotFoundException(Class<?> entityClass, String parameters) {
        super("error.application.no-resource");
        this.entityClass = entityClass;
        this.parameters = parameters;
    }

}
