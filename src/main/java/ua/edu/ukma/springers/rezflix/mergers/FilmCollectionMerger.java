package ua.edu.ukma.springers.rezflix.mergers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpsertFilmCollectionDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCollectionEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;

import java.util.HashSet;

import static ua.edu.ukma.springers.rezflix.mergers.MergerUtils.mergeRelatedIds;

@Component
@RequiredArgsConstructor
public class FilmCollectionMerger implements IMerger<FilmCollectionEntity, UpsertFilmCollectionDto, UpsertFilmCollectionDto> {

    private final FilmRepository filmRepository;

    @Override
    public void mergeForCreate(FilmCollectionEntity entity, UpsertFilmCollectionDto view) {
        merge(entity, view);
    }

    @Override
    public void mergeForUpdate(FilmCollectionEntity entity, UpsertFilmCollectionDto view) {
        merge(entity, view);
    }

    private void merge(FilmCollectionEntity entity, UpsertFilmCollectionDto view) {
        entity.setName(view.getName());
        entity.setDescription(view.getDescription());
        mergeRelatedIds(
            view.getFilmIds(), filmRepository, l -> entity.setFilms(new HashSet<>(l)),
            () -> new ValidationException("error.film_collection.film.not_existent")
        );
    }
}
