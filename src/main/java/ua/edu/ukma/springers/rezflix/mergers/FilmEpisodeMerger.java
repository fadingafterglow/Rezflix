package ua.edu.ukma.springers.rezflix.mergers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateEpisodeDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpdateEpisodeDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEpisodeEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmDubbingRepository;

import static ua.edu.ukma.springers.rezflix.mergers.MergerUtils.mergeRelatedId;

@Component
@RequiredArgsConstructor
public class FilmEpisodeMerger implements IMerger<FilmEpisodeEntity, CreateEpisodeDto, UpdateEpisodeDto> {

    private final FilmDubbingRepository filmDubbingRepository;

    @Override
    public void mergeForCreate(FilmEpisodeEntity entity, CreateEpisodeDto view) {
        mergeRelatedId(
            view.getFilmDubbingId(), filmDubbingRepository, entity::setFilmDubbing,
            () -> new ValidationException("error.film_episode_film_dubbing.not_existent")
        );
        entity.setFilmDubbingId(view.getFilmDubbingId());
        merge(entity, view.getMetadata());
    }

    @Override
    public void mergeForUpdate(FilmEpisodeEntity entity, UpdateEpisodeDto view) {
        merge(entity, view);
    }

    private void merge(FilmEpisodeEntity entity, UpdateEpisodeDto view) {
        entity.setWatchOrder(view.getWatchOrder());
        entity.setTitle(view.getTitle());
    }
}
