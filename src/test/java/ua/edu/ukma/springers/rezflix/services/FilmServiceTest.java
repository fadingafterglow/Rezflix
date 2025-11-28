package ua.edu.ukma.springers.rezflix.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.criteria.FilmCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.mappers.FilmMapper;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FilmServiceTest extends BaseServiceTest<FilmService, FilmEntity, UpsertFilmDto, UpsertFilmDto, Integer> {
    @Mock private FilmRepository filmRepository;
    @Mock private FilmMapper mapper;
    @Mock private FilmRatingService filmRatingService;
    private static final int FILM_ID = 1;

    @Override
    protected FilmService createService() {
        this.repository = filmRepository;
        return new FilmService(mapper, filmRepository, filmRatingService);
    }

    @Test
    @DisplayName("Should return film DTO with user ratings")
    void getResponseById() {
        FilmEntity entity = new FilmEntity();
        entity.setId(FILM_ID);
        FilmDto expectedDto = new FilmDto();
        Map<Integer, FilmRatingDto> ratings = Map.of(FILM_ID, new FilmRatingDto());
        when(repository.findFetchAllById(FILM_ID)).thenReturn(Optional.of(entity));
        when(filmRatingService.getCurrentUserRatingForFilms(List.of(FILM_ID))).thenReturn(ratings);
        when(mapper.toResponse(entity, ratings)).thenReturn(expectedDto);
        FilmDto result = service.getResponseById(FILM_ID);
        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(validator).validForView(entity);
    }

    @Test
    @DisplayName("Should return film list DTO with user ratings based on criteria")
    void getListResponseByCriteria() {
        FilmCriteriaDto criteriaDto = new FilmCriteriaDto();
        FilmEntity entity = new FilmEntity();
        entity.setId(FILM_ID);
        List<FilmEntity> entities = List.of(entity);
        Map<Integer, FilmRatingDto> ratings = Map.of(FILM_ID, new FilmRatingDto());
        FilmListDto expectedListDto = new FilmListDto();
        when(criteriaRepository.find(any(FilmCriteria.class))).thenReturn(entities);
        when(criteriaRepository.count(any(FilmCriteria.class))).thenReturn(1L);
        when(filmRatingService.getCurrentUserRatingForFilms(List.of(FILM_ID))).thenReturn(ratings);
        when(mapper.toListResponse(1L, entities, ratings)).thenReturn(expectedListDto);
        FilmListDto result = service.getListResponseByCriteria(criteriaDto);
        assertEquals(expectedListDto, result);
    }

    @Test
    @DisplayName("Should trigger repository recalculation of total ratings")
    void recalculateTotalRatings() {
        service.recalculateTotalRatings();
        verify(filmRepository).recalculateTotalRatings();
    }

    @Test
    @DisplayName("Should return correct cache name")
    void getCacheName() {
        assertEquals("film", service.getCacheName());
    }
}