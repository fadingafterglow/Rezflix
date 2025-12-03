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

    interface FILM_DUBBING_API {
        String BASE = "/api/film/dubbing";
        String ID = "/{id}";
    }

    interface FILM_COMMENT_API {
        String BASE = "/api/film/comment";
        String ID = "/{id}";
    }

    interface FILM_API {
        String BASE = "/api/film";
        String ID = "/{id}";
    }

    interface FILM_COLLECTION_API {
        String BASE = "/api/film-collections";
        String ID = "/{id}";
    }

    interface FILM_EPISPODES_API {
        String BASE = "/api/film/episode";
        String EPISODS = "/api/film/dubbing/{dubbingId}/episodes";
        String ID = "/{id}";
    }

    interface FILM_RECOMENDATION_API {
        String BASE = "/api/film/recommendations";
    }

    interface FILM_WATCH_API {
        String BASE = "/api/watch-room";
    }

    interface FILM_RATING_API {
        String BASE = "/api/film/{filmId}/rating";
    }
}
