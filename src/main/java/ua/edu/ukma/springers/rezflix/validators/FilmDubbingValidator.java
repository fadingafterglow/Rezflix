package ua.edu.ukma.springers.rezflix.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmDubbingEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmDubbingRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FilmDubbingValidator extends BaseValidator<FilmDubbingEntity> {

    private final FilmDubbingRepository filmDubbingRepository;

    @Override
    public void validForCreate(FilmDubbingEntity entity) {
        super.validForCreate(entity);
        validateNameIsUniqueForFilm(entity);
    }

    @Override
    public void validForUpdate(FilmDubbingEntity entity) {
        super.validForUpdate(entity);
        validateNameIsUniqueForFilm(entity);
    }

    private void validateNameIsUniqueForFilm(FilmDubbingEntity entity) {
        boolean isUnique = filmDubbingRepository.findIdByFilmIdAndName(entity.getFilmId(), entity.getName())
                .map(id -> Objects.equals(entity.getId(), id))
                .orElse(true);
        if (!isUnique)
            throw new ValidationException("error.film_dubbing.name.duplicate");
    }
}
