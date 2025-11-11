package ua.edu.ukma.springers.rezflix.utils;

public interface ApiPaths {

    interface AUTH_API {
        String BASE = "/auth";
        String LOGIN = "/login";
        String REFRESH = "/refresh";
    }

    interface USER_API {
        String BASE = "/api/user";
        String REGISTER = "/register";
        String CURRENT = "/current";
    }
}
