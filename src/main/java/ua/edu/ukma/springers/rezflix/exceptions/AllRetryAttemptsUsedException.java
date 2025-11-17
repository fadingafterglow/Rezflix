package ua.edu.ukma.springers.rezflix.exceptions;

public class AllRetryAttemptsUsedException extends BaseException {
    public AllRetryAttemptsUsedException() {
        super("error.application.service-unavailable");
    }
}
