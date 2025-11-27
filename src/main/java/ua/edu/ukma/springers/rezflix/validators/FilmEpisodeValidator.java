package ua.edu.ukma.springers.rezflix.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEpisodeEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.FilmEpisodeStatus;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.exceptions.NotFoundException;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmEpisodeRepository;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FilmEpisodeValidator extends BaseValidator<FilmEpisodeEntity> {

    private final FilmEpisodeRepository filmEpisodeRepository;
    private final SecurityUtils securityUtils;

    @Override
    public void validForView(FilmEpisodeEntity entity) {
        super.validForView(entity);
        if (entity.getStatus() != FilmEpisodeStatus.RENDERED && !securityUtils.hasRole(UserRole.CONTENT_MANAGER))
            throw new NotFoundException(FilmEpisodeEntity.class, "id: " + entity.getId());
    }

    @Override
    public void validForCreate(FilmEpisodeEntity entity) {
        super.validForCreate(entity);
        validateWatchOrderIsUniqueForDubbing(entity);
    }

    @Override
    public void validForUpdate(FilmEpisodeEntity entity) {
        super.validForUpdate(entity);
        validateWatchOrderIsUniqueForDubbing(entity);
    }

    private void validateWatchOrderIsUniqueForDubbing(FilmEpisodeEntity entity) {
        boolean isUnique = filmEpisodeRepository.findIdByFilmDubbingIdAndWatchOrder(entity.getFilmDubbingId(), entity.getWatchOrder())
                .map(id -> Objects.equals(entity.getId(), id))
                .orElse(true);
        if (!isUnique)
            throw new ValidationException("error.film_episode.watch_order.duplicate");
    }
}
