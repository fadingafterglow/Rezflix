package ua.edu.ukma.springers.rezflix.services;

import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmRatingDto;
import org.springframework.stereotype.Service;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingEntity;
import ua.edu.ukma.springers.rezflix.domain.embeddables.FilmRatingId;
import ua.edu.ukma.springers.rezflix.exceptions.NotFoundException;
import ua.edu.ukma.springers.rezflix.mappers.FilmRatingMapper;
import ua.edu.ukma.springers.rezflix.utils.SecurityUtils;

@Service
public class FilmRatingService extends BaseCRUDService<FilmRatingEntity, FilmRatingDto, FilmRatingDto, FilmRatingId> {

    private final SecurityUtils securityUtils;
    private final FilmRatingMapper mapper;

    protected FilmRatingService(SecurityUtils securityUtils, FilmRatingMapper mapper) {
        super(FilmRatingEntity.class, FilmRatingEntity::new);
        this.securityUtils = securityUtils;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public FilmRatingDto getCurrentUserFilmRating(int filmId) {
        return mapper.toResponse(getById(getCurrentUserFilmRatingId(filmId)));
    }

    @Transactional
    public FilmRatingDto setCurrentUserFilmRating(int filmId, FilmRatingDto dto) {
        FilmRatingId id = getCurrentUserFilmRatingId(filmId);
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
    public void deleteCurrentUserFilmRating(int filmId) {
        delete(getCurrentUserFilmRatingId(filmId));
    }

    private FilmRatingId getCurrentUserFilmRatingId(int filmId) {
        return new FilmRatingId(filmId, securityUtils.getCurrentUserId());
    }

}