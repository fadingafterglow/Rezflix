package ua.edu.ukma.springers.rezflix.exceptions;

public class RateLimitExceededException extends BaseException {
    public RateLimitExceededException() {
        super("error.application.rate-limit");
    }
}
