package ua.edu.ukma.springers.rezflix.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCommentEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;

@Component
@RequiredArgsConstructor
public class FilmCommentValidator extends BaseValidator<FilmCommentEntity> {
    private final UserRepository userRepo;
    private final FilmRepository filmRepo;

    @Override
    public void validForCreate(FilmCommentEntity entity) {
        super.validForCreate(entity);
        if (!filmRepo.existsById(entity.getFilm().getId()))
            throw new ValidationException("error.film_comment.film.not_existent");
        if (!userRepo.existsById(entity.getUser().getId()))
            throw new ValidationException("error.film_comment.user.not_existent");
    }
}
