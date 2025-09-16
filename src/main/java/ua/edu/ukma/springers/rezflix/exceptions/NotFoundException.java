package ua.edu.ukma.springers.rezflix.exceptions;

import lombok.Getter;

@Getter
public class NotFoundException extends BaseException {

    private Class<?> entityClass;
    private String parameters;

    public NotFoundException() {
        super("error.application.no-resource");
    }

    public NotFoundException(Class<?> entityClass, String parameters) {
        this();
        this.entityClass = entityClass;
        this.parameters = parameters;
    }

}
