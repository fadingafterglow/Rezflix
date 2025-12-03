package ua.edu.ukma.springers.rezflix.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateDubbingDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.DubbingCriteriaDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.DubbingDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.DubbingListDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpdateDubbingDto;
import ua.edu.ukma.springers.rezflix.criteria.FilmDubbingCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmDubbingEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.events.DeleteEntityEvent;
import ua.edu.ukma.springers.rezflix.mappers.FilmDubbingMapper;
import ua.edu.ukma.springers.rezflix.repositories.FilmDubbingRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FilmDubbingServiceTest extends BaseServiceTest<FilmDubbingService, FilmDubbingEntity, CreateDubbingDto, UpdateDubbingDto, Integer> {

    @Mock private FilmDubbingRepository dubbingRepository;
    @Mock private FilmDubbingMapper mapper;

    @Override
    protected FilmDubbingService createService() {
        this.repository = dubbingRepository;
        return new FilmDubbingService(mapper);
    }

    @Test
    @DisplayName("Should return dubbing DTO by ID")
    void getResponseById() {
        int id = 1;
        FilmDubbingEntity entity = new FilmDubbingEntity();
        entity.setId(id);
        DubbingDto dto = new DubbingDto();

        when(repository.findFetchAllById(id)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(dto);

        DubbingDto result = service.getResponseById(id);
        assertEquals(dto, result);
    }

    @Test
    @DisplayName("Should return list of dubbings by criteria")
    void getListResponseByCriteria() {
        DubbingCriteriaDto criteriaDto = new DubbingCriteriaDto();
        List<FilmDubbingEntity> entities = List.of(new FilmDubbingEntity());
        DubbingListDto listDto = new DubbingListDto();

        when(criteriaRepository.find(any(FilmDubbingCriteria.class))).thenReturn(entities);
        when(criteriaRepository.count(any(FilmDubbingCriteria.class))).thenReturn(1L);
        when(mapper.toListResponse(1L, entities)).thenReturn(listDto);

        DubbingListDto result = service.getListResponseByCriteria(criteriaDto);
        assertEquals(listDto, result);
    }

    @Test
    @DisplayName("Should delete dubbings when film is deleted")
    void clearDubbings() {
        int filmId = 100;
        FilmEntity film = new FilmEntity();
        film.setId(filmId);
        DeleteEntityEvent<FilmEntity, Integer> event = new DeleteEntityEvent<>(film);

        FilmDubbingEntity dubbing = new FilmDubbingEntity();
        dubbing.setId(1);

        when(dubbingRepository.findAllByFilmId(filmId)).thenReturn(List.of(dubbing));

        service.clearDubbings(event);

        verify(dubbingRepository).delete(dubbing);
    }
}