package ua.edu.ukma.springers.rezflix.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;

@Component
@RequiredArgsConstructor
public class FilmRatingValidator extends BaseValidator<FilmRatingEntity>{

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    @Override
    public void validForCreate(FilmRatingEntity entity) {
        super.validForCreate(entity);
        if (!filmRepository.existsById(entity.getId().getFilmId()))
            throw new ValidationException("error.film_rating.film.not_existent");
        if (!userRepository.existsById(entity.getId().getUserId()))
            throw new ValidationException("error.film_rating.user.not_existent");
    }
}
