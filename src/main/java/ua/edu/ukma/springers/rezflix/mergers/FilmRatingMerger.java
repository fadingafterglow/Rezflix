package ua.edu.ukma.springers.rezflix.mergers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmRatingDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingEntity;

@Component
@RequiredArgsConstructor
public class FilmRatingMerger implements IMerger<FilmRatingEntity, FilmRatingDto, FilmRatingDto> {
    @Override
    public void mergeForCreate(FilmRatingEntity entity, FilmRatingDto view) {

    }

    @Override
    public void mergeForUpdate(FilmRatingEntity entity, FilmRatingDto view) {

    }
}
