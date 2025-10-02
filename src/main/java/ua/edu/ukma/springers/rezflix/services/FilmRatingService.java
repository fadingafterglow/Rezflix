package ua.edu.ukma.springers.rezflix.services;

import jakarta.transaction.Transactional;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmRatingDto;
import org.springframework.stereotype.Service;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingId;
import ua.edu.ukma.springers.rezflix.exceptions.NotFoundException;
import ua.edu.ukma.springers.rezflix.mappers.FilmRatingMapper;


@Service
public class FilmRatingService extends BaseCRUDService<FilmRatingEntity, FilmRatingDto, FilmRatingDto, FilmRatingId> {

    private final FilmRatingMapper mapper;

    protected FilmRatingService(FilmRatingMapper mapper) {
        super(FilmRatingEntity.class, FilmRatingEntity::new);
        this.mapper = mapper;
    }

    @Transactional
    public FilmRatingDto getRatingByFilmIdAndUserId(Integer filmId, Integer userId) {
        FilmRatingId id = new FilmRatingId(filmId, userId);
        return mapper.toResponse(getById(id));
    }

    @Transactional
    public FilmRatingDto putFilmRating(Integer filmId, Integer userId, FilmRatingDto dto) {
        FilmRatingId id = new FilmRatingId(filmId, userId);
        FilmRatingEntity entity;
        try {
            update(id, dto);
            entity = getById(id);
        } catch (NotFoundException e) {
            entity = createEntity(dto);
            entity.setId(id);
            repository.save(entity);
        }
        return mapper.toResponse(entity);
    }

   @Transactional
    public void deleteFilmRating(Integer filmId, Integer userId) {
        FilmRatingId id = new FilmRatingId(filmId, userId);
        delete(id);
    }


}