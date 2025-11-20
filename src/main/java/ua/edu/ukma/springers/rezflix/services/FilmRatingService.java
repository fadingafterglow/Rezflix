package ua.edu.ukma.springers.rezflix.services;

import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmRatingDto;
import org.springframework.stereotype.Service;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingEntity;
import ua.edu.ukma.springers.rezflix.domain.embeddables.FilmRatingId;
import ua.edu.ukma.springers.rezflix.mappers.FilmRatingMapper;

@Service
public class FilmRatingService extends BaseCRUDService<FilmRatingEntity, FilmRatingDto, FilmRatingDto, FilmRatingId> {

    private final FilmRatingMapper mapper;

    protected FilmRatingService(FilmRatingMapper mapper) {
        super(FilmRatingEntity.class, FilmRatingEntity::new);
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public FilmRatingDto getUserRatingForFilm(int userId, int filmId) {
        return mapper.toResponse(getById(new FilmRatingId(filmId, userId)));
    }

    @Transactional
    public void setUserRatingForFilm(int userId, int filmId, FilmRatingDto dto) {
        FilmRatingId id = new FilmRatingId(filmId, userId);
        if (repository.existsById(id))
            update(id, dto);
        else
            create(id, dto);
    }

    @Transactional
    public void deleteUserRatingForFilm(int userId, int filmId) {
        delete(new FilmRatingId(filmId, userId));
    }

    @Override
    public String getCacheName() {
        return "filmRating";
    }
}