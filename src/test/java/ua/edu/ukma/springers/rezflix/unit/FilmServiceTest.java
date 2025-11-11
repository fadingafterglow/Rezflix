package ua.edu.ukma.springers.rezflix.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.criteria.core.Criteria;
import ua.edu.ukma.criteria.core.CriteriaRepository;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCriteriaDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmListDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpsertFilmDto;
import ua.edu.ukma.springers.rezflix.criteria.FilmCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.mappers.FilmMapper;
import ua.edu.ukma.springers.rezflix.mergers.IMerger;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;
import ua.edu.ukma.springers.rezflix.services.FilmService;
import ua.edu.ukma.springers.rezflix.validators.IValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FilmServiceTest {

    @Mock
    private FilmRepository filmRepository;
    @Mock
    private CriteriaRepository criteriaRepository;
    @Mock
    private IValidator<FilmEntity> validator;
    @Mock
    private IMerger<FilmEntity, UpsertFilmDto, UpsertFilmDto> merger;
    @Mock
    private FilmMapper filmMapper;

    @InjectMocks
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        filmService.setRepository(filmRepository);
        filmService.setCriteriaRepository(criteriaRepository);
        filmService.setValidator(validator);
        filmService.setMerger(merger);
    }

    @Test
    void getByIdTest() {
        Integer filmId = 1;
        FilmEntity mockFilm = new FilmEntity();
        mockFilm.setId(filmId);

        when(filmRepository.findFetchAllById(filmId)).thenReturn(Optional.of(mockFilm));
        doNothing().when(validator).validForView(mockFilm);

        FilmEntity result = filmService.getById(filmId);

        assertNotNull(result);
        assertEquals(filmId, result.getId());
        verify(filmRepository, times(1)).findFetchAllById(filmId);
        verify(validator, times(1)).validForView(mockFilm);
    }

    @Test
    void deleteTest() {
        Integer filmId = 1;
        FilmEntity mockFilm = new FilmEntity();
        mockFilm.setId(filmId);

        when(filmRepository.findById(filmId)).thenReturn(Optional.of(mockFilm));
        doNothing().when(validator).validForDelete(mockFilm);
        doNothing().when(filmRepository).delete(mockFilm);

        filmService.delete(filmId);

        verify(filmRepository, times(1)).findById(filmId);
        verify(validator, times(1)).validForDelete(mockFilm);
        verify(filmRepository, times(1)).delete(mockFilm);
    }

    @Test
    void updateTest() {
        Integer filmId = 1;
        UpsertFilmDto updateDto = new UpsertFilmDto();
        updateDto.setTitle("New Title");

        FilmEntity existingFilm = new FilmEntity();
        existingFilm.setId(filmId);
        existingFilm.setTitle("Old Title");

        when(filmRepository.findById(filmId)).thenReturn(Optional.of(existingFilm));
        doNothing().when(merger).mergeForUpdate(existingFilm, updateDto);
        doNothing().when(validator).validForUpdate(existingFilm);
        when(filmRepository.save(existingFilm)).thenReturn(existingFilm);

        boolean result = filmService.update(filmId, updateDto);

        assertTrue(result);
        verify(filmRepository, times(1)).findById(filmId);
        verify(merger, times(1)).mergeForUpdate(existingFilm, updateDto);
        verify(validator, times(1)).validForUpdate(existingFilm);
        verify(filmRepository, times(1)).save(existingFilm);
    }

    @Test
    void countTest(@Mock Criteria<FilmEntity, ?> mockCriteria) {
        long expectedCount = 42L;
        when(criteriaRepository.count(mockCriteria)).thenReturn(expectedCount);

        long actualCount = filmService.count(mockCriteria);

        assertEquals(expectedCount, actualCount);
        verify(criteriaRepository, times(1)).count(mockCriteria);
    }

    @Test
    void createTest() {
        UpsertFilmDto createDto = new UpsertFilmDto();
        createDto.setTitle("New Film");

        doNothing().when(merger).mergeForCreate(any(FilmEntity.class), eq(createDto));
        doNothing().when(validator).validForCreate(any(FilmEntity.class));

        when(filmRepository.save(any(FilmEntity.class))).thenAnswer(invocation -> {
            FilmEntity entity = invocation.getArgument(0);
            entity.setId(1);
            return entity;
        });

        Integer newId = filmService.create(createDto);

        assertEquals(1, newId);
        verify(merger, times(1)).mergeForCreate(any(FilmEntity.class), eq(createDto));
        verify(validator, times(1)).validForCreate(any(FilmEntity.class));
        verify(filmRepository, times(1)).save(any(FilmEntity.class));
    }

    @Test
    void createWithIdTest() {
        Integer filmId = 99;
        UpsertFilmDto createDto = new UpsertFilmDto();

        doNothing().when(merger).mergeForCreate(any(FilmEntity.class), eq(createDto));
        doNothing().when(validator).validForCreate(any(FilmEntity.class));

        when(filmRepository.save(any(FilmEntity.class))).thenAnswer(invocation -> {
            FilmEntity entity = invocation.getArgument(0);
            assertEquals(filmId, entity.getId());
            return entity;
        });

        Integer newId = filmService.create(filmId, createDto);

        assertEquals(filmId, newId);
        verify(merger, times(1)).mergeForCreate(any(FilmEntity.class), eq(createDto));
        verify(validator, times(1)).validForCreate(any(FilmEntity.class));
        verify(filmRepository, times(1)).save(any(FilmEntity.class));
    }

    @Test
    void getListTest(@Mock Criteria<FilmEntity, ?> mockCriteria) {
        FilmEntity film1 = new FilmEntity();
        film1.setId(1);
        List<FilmEntity> mockList = List.of(film1);

        when(criteriaRepository.find(mockCriteria)).thenReturn(mockList);
        doNothing().when(validator).validForView(mockList);

        List<FilmEntity> result = filmService.getList(mockCriteria);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(criteriaRepository, times(1)).find(mockCriteria);
        verify(validator, times(1)).validForView(mockList);
    }

    @Test
    void getResponseByIdTest() {
        Integer filmId = 1;
        FilmEntity mockFilm = new FilmEntity();
        mockFilm.setId(filmId);

        FilmDto mockDto = new FilmDto();
        mockDto.setId(filmId);

        when(filmRepository.findFetchAllById(filmId)).thenReturn(Optional.of(mockFilm));
        doNothing().when(validator).validForView(mockFilm);
        when(filmMapper.toResponse(mockFilm)).thenReturn(mockDto);

        FilmDto resultDto = filmService.getResponseById(filmId);

        assertNotNull(resultDto);
        assertEquals(filmId, resultDto.getId());
        verify(filmRepository, times(1)).findFetchAllById(filmId);
        verify(validator, times(1)).validForView(mockFilm);
        verify(filmMapper, times(1)).toResponse(mockFilm);
    }

    @Test
    void getListResponseByCriteriaTest() {
        FilmCriteriaDto criteriaDto = new FilmCriteriaDto();
        List<FilmEntity> mockList = List.of(new FilmEntity());
        long expectedCount = 1L;
        FilmListDto mockListDto = new FilmListDto();

        when(criteriaRepository.find(any(FilmCriteria.class))).thenReturn(mockList);
        doNothing().when(validator).validForView(mockList);
        when(criteriaRepository.count(any(FilmCriteria.class))).thenReturn(expectedCount);
        when(filmMapper.toListResponse(expectedCount, mockList)).thenReturn(mockListDto);

        FilmListDto resultDto = filmService.getListResponseByCriteria(criteriaDto);

        assertNotNull(resultDto);
        verify(criteriaRepository, times(1)).find(any(FilmCriteria.class));
        verify(validator, times(1)).validForView(mockList);
        verify(criteriaRepository, times(1)).count(any(FilmCriteria.class));
        verify(filmMapper, times(1)).toListResponse(expectedCount, mockList);
    }
}