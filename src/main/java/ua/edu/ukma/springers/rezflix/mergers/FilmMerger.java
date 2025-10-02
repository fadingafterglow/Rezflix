package ua.edu.ukma.springers.rezflix.mergers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpsertFilmDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
@Component
@RequiredArgsConstructor
public class FilmMerger implements IMerger<FilmEntity, UpsertFilmDto, UpsertFilmDto> {
    @Override
    public void mergeForCreate(FilmEntity entity, UpsertFilmDto view) {}

    @Override
    public void mergeForUpdate(FilmEntity entity, UpsertFilmDto view) {

    }
}
