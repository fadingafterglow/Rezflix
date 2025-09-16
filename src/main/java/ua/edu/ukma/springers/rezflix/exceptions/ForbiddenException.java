package ua.edu.ukma.springers.rezflix.exceptions;

public class ForbiddenException extends BaseException {
    public ForbiddenException() {
        super("error.application.forbidden");
    }
}
