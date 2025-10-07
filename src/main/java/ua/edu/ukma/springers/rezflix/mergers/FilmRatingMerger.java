package ua.edu.ukma.springers.rezflix.mergers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmRatingDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingEntity;
import ua.edu.ukma.springers.rezflix.utils.TimeUtils;

@Component
@RequiredArgsConstructor
public class FilmRatingMerger implements IMerger<FilmRatingEntity, FilmRatingDto, FilmRatingDto> {

    @Override
    public void mergeForCreate(FilmRatingEntity entity, FilmRatingDto view) {
        merge(entity, view);
        entity.setCreatedAt(TimeUtils.getCurrentDateTimeUTC());
    }

    @Override
    public void mergeForUpdate(FilmRatingEntity entity, FilmRatingDto view) {
        merge(entity, view);
        entity.setUpdatedAt(TimeUtils.getCurrentDateTimeUTC());
    }

    private void merge(FilmRatingEntity entity, FilmRatingDto view) {
        entity.setRating(view.getRating());
    }
}
