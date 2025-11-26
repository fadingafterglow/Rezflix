package ua.edu.ukma.springers.rezflix.mergers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateDubbingDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpdateDubbingDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmDubbingEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;

import static ua.edu.ukma.springers.rezflix.mergers.MergerUtils.mergeRelatedId;

@Component
@RequiredArgsConstructor
public class FilmDubbingMerger implements IMerger<FilmDubbingEntity, CreateDubbingDto, UpdateDubbingDto> {

    private final FilmRepository filmRepo;

    @Override
    public void mergeForCreate(FilmDubbingEntity entity, CreateDubbingDto view) {
        mergeRelatedId(
            view.getFilmId(), filmRepo, entity::setFilm,
            () -> new ValidationException("error.film_dubbing.film.not_existent")
        );
        entity.setFilmId(view.getFilmId());
        entity.setName(view.getName());
    }

    @Override
    public void mergeForUpdate(FilmDubbingEntity entity, UpdateDubbingDto view) {
        entity.setName(view.getName());
    }
}
