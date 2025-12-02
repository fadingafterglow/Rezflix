package ua.edu.ukma.springers.rezflix.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmRatingDto;
import ua.edu.ukma.springers.rezflix.domain.embeddables.FilmRatingId;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingEntity;
import ua.edu.ukma.springers.rezflix.mappers.FilmRatingMapper;
import ua.edu.ukma.springers.rezflix.repositories.FilmRatingRepository;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FilmRatingServiceTest extends BaseServiceTest<FilmRatingService, FilmRatingEntity, FilmRatingDto, FilmRatingDto, FilmRatingId> {
    @Mock private FilmRatingRepository filmRatingRepository;
    @Mock private FilmRatingMapper mapper;
    @Mock private SecurityUtils securityUtils;
    private final int USER_ID = 42;
    private final int FILM_ID = 101;
    private final FilmRatingId ratingId = new FilmRatingId(FILM_ID, USER_ID);
    private final FilmRatingEntity ratingEntity = new FilmRatingEntity(ratingId, null, null, 5, null, null);
    private final FilmRatingDto ratingDto = new FilmRatingDto(5);

    @Override
    protected FilmRatingService createService() {
        this.repository = filmRatingRepository;
        return new FilmRatingService(mapper, securityUtils);
    }

    @Test
    @DisplayName("Should return rating DTO for specific film and user")
    void getUserRatingForFilm() {
        when(repository.findFetchAllById(ratingId)).thenReturn(Optional.of(ratingEntity));
        when(mapper.toResponse(ratingEntity)).thenReturn(ratingDto);
        FilmRatingDto result = service.getUserRatingForFilm(USER_ID, FILM_ID);
        assertEquals(ratingDto, result);
        verify(validator).validForView(ratingEntity);
    }

    @Test
    @DisplayName("Should return map of ratings for current user")
    void getCurrentUserRatingForFilms_WithUser() {
        List<Integer> filmIds = List.of(FILM_ID);
        when(securityUtils.getCurrentUserId()).thenReturn(USER_ID);
        when(filmRatingRepository.findByUserIdAndFilmIdIn(USER_ID, filmIds)).thenReturn(List.of(ratingEntity));
        when(mapper.toResponse(ratingEntity)).thenReturn(ratingDto);
        Map<Integer, FilmRatingDto> result = service.getCurrentUserRatingForFilms(filmIds);
        assertEquals(1, result.size());
        assertEquals(ratingDto, result.get(FILM_ID));
    }

    @Test
    @DisplayName("Should update existing rating and evict cache")
    void setUserRatingForFilm_Update() {
        when(repository.existsById(ratingId)).thenReturn(true);
        when(repository.findById(ratingId)).thenReturn(Optional.of(ratingEntity));
        when(cacheManager.getCache("filmRating")).thenReturn(cache);
        service.setUserRatingForFilm(USER_ID, FILM_ID, ratingDto);
        verify(merger).mergeForUpdate(ratingEntity, ratingDto);
        verify(validator).validForUpdate(ratingEntity);
        verify(repository).save(ratingEntity);
        verify(cache).evict(ratingId);
    }

    @Test
    @DisplayName("Should create new rating if it does not exist")
    void setUserRatingForFilm_Create() {
        when(repository.existsById(ratingId)).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.setUserRatingForFilm(USER_ID, FILM_ID, ratingDto);

        verify(merger).mergeForCreate(any(FilmRatingEntity.class), eq(ratingDto));
        verify(validator).validForCreate(any(FilmRatingEntity.class));
        verify(repository).save(any(FilmRatingEntity.class));
    }

    @Test
    @DisplayName("Should return empty map when user is anonymous")
    void getCurrentUserRatingForFilms_Anonymous() {
        when(securityUtils.getCurrentUserId()).thenReturn(null);
        Map<Integer, FilmRatingDto> result = service.getCurrentUserRatingForFilms(List.of(1));
        assertTrue(result.isEmpty());
        verify(filmRatingRepository, never()).findByUserIdAndFilmIdIn(anyInt(), any());
    }

    @Test
    @DisplayName("Should delete rating and evict cache")
    void deleteUserRatingForFilm() {
        when(repository.findById(ratingId)).thenReturn(Optional.of(ratingEntity));
        when(cacheManager.getCache("filmRating")).thenReturn(cache);
        service.deleteUserRatingForFilm(USER_ID, FILM_ID);
        verify(repository).delete(ratingEntity);
        verify(cache).evict(ratingId);
    }
}