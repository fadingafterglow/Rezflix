package ua.edu.ukma.springers.rezflix.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FilmValidator extends BaseValidator<FilmEntity> {

    private final FilmRepository filmRepository;

    @Override
    public void validForCreate(FilmEntity entity) {
        super.validForCreate(entity);
        validateTitleIsUnique(entity);
    }

    @Override
    public void validForUpdate(FilmEntity entity) {
        super.validForUpdate(entity);
        validateTitleIsUnique(entity);
    }

    private void validateTitleIsUnique(FilmEntity entity) {
        boolean isUnique = filmRepository.findIdByTitle(entity.getTitle())
                .map(id -> Objects.equals(entity.getId(), id))
                .orElse(true);
        if (!isUnique)
            throw new ValidationException("error.film.title.duplicate");
    }
}
