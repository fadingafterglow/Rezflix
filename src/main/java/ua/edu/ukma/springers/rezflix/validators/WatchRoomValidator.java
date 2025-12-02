package ua.edu.ukma.springers.rezflix.validators;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.domain.entities.WatchRoomEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.FilmEpisodeStatus;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmEpisodeRepository;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class WatchRoomValidator {

    private final Validator validator;
    private final FilmEpisodeRepository filmEpisodeRepository;

    public void validForCreate(WatchRoomEntity entity) {
        validateData(entity);
        if (!filmEpisodeRepository.existsByIdAndStatus(entity.getEpisodeId(), FilmEpisodeStatus.RENDERED))
            throw new ValidationException("error.watch_room.episode.not_existent");
    }

    protected void validateData(WatchRoomEntity entity) {
        Set<ConstraintViolation<WatchRoomEntity>> violations = validator.validate(entity);
        if (violations != null && !violations.isEmpty())
            throw new ValidationException(violations);
    }
}
