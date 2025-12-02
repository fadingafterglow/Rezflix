package ua.edu.ukma.springers.rezflix.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;

@Getter
@RequiredArgsConstructor
public enum FileType {
    USER_AVATAR(true, UserEntity.class),
    FILM_POSTER(false, FilmEntity.class);

    private final boolean isUnique;
    private final Class<?> entityClass;
}