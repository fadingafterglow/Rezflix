package ua.edu.ukma.springers.rezflix.exceptions;

public class UnauthenticatedException extends BaseException {
    public UnauthenticatedException() {
        super("error.application.unauthenticated");
    }
}
