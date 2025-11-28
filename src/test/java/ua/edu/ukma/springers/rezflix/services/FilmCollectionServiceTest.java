package ua.edu.ukma.springers.rezflix.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.criteria.FilmCollectionCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCollectionEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.mappers.FilmCollectionMapper;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FilmCollectionServiceTest extends BaseServiceTest<FilmCollectionService, FilmCollectionEntity, UpsertFilmCollectionDto, UpsertFilmCollectionDto, Integer> {
    @Mock private FilmCollectionMapper mapper;
    @Mock private FilmRatingService filmRatingService;
    @Mock private SecurityUtils securityUtils;
    private static final int COLLECTION_ID = 1;
    private static final int FILM_ID = 10;
    private static final int USER_ID = 123;
    private FilmCollectionEntity entity;
    private FilmEntity film;
    private UserEntity user;
    private UpsertFilmCollectionDto upsertDto;
    private FilmCollectionDto responseDto;
    private FilmCollectionListDto listDto;
    private FilmCollectionCriteriaDto criteriaDto;
    private Map<Integer, FilmRatingDto> ratingsMap;

    @Override
    protected FilmCollectionService createService() {
        return new FilmCollectionService(mapper, filmRatingService, securityUtils);
    }

    @BeforeEach
    void setUpData() {
        film = new FilmEntity();
        film.setId(FILM_ID);
        user = new UserEntity();
        user.setId(USER_ID);
        entity = new FilmCollectionEntity();
        entity.setId(COLLECTION_ID);
        entity.setFilms(Set.of(film));
        upsertDto = new UpsertFilmCollectionDto();
        responseDto = new FilmCollectionDto();
        listDto = new FilmCollectionListDto();
        criteriaDto = new FilmCollectionCriteriaDto();
        ratingsMap = Map.of(FILM_ID, new FilmRatingDto());
    }

    @Test
    @DisplayName("Should return collection DTO when entity exists")
    void getResponseById() {
        when(repository.findFetchAllById(COLLECTION_ID)).thenReturn(Optional.of(entity));
        when(filmRatingService.getCurrentUserRatingForFilms(List.of(FILM_ID))).thenReturn(ratingsMap);
        when(mapper.toResponse(entity, ratingsMap)).thenReturn(responseDto);
        FilmCollectionDto result = service.getResponseById(COLLECTION_ID);
        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(validator).validForView(entity);
        verify(mapper).toResponse(entity, ratingsMap);
    }

    @Test
    @DisplayName("Should return collection list DTO based on criteria")
    void getListResponseByCriteria() {
        List<FilmCollectionEntity> entities = List.of(entity);
        long total = 1L;
        when(criteriaRepository.find(any(FilmCollectionCriteria.class))).thenReturn(entities);
        when(criteriaRepository.count(any(FilmCollectionCriteria.class))).thenReturn(total);
        when(mapper.toListResponse(total, entities)).thenReturn(listDto);
        FilmCollectionListDto result = service.getListResponseByCriteria(criteriaDto);
        assertEquals(listDto, result);
        verify(validator).validForView(entities);
    }

    @Test
    @DisplayName("Should set current user as owner during creation")
    void postCreate() {
        when(securityUtils.getCurrentUser()).thenReturn(user);
        service.postCreate(entity, upsertDto);
        assertEquals(user, entity.getOwner());
        verify(securityUtils).getCurrentUser();
    }
}