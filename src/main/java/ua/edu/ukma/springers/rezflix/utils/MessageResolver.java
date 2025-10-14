package ua.edu.ukma.springers.rezflix.utils;

public interface MessageResolver {

    String resolve(String message, Object... args);
}
