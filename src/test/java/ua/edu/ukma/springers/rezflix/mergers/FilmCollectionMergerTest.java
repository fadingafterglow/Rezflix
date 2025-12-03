package ua.edu.ukma.springers.rezflix.mergers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpsertFilmCollectionDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCollectionEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;
import java.util.HashSet;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmCollectionMergerTest {

    @Mock private FilmRepository filmRepository;
    @Mock private FilmCollectionEntity entity;
    @Mock private UpsertFilmCollectionDto dto;
    private FilmCollectionMerger merger;

    @BeforeEach
    void setUp() {
        merger = new FilmCollectionMerger(filmRepository);
        when(dto.getName()).thenReturn("Coll");
        when(dto.getDescription()).thenReturn("Desc");
        List<Integer> ids = List.of(1, 2);
        when(dto.getFilmIds()).thenReturn(ids);
    }

    @Test
    @DisplayName("Should merge name, description and existing films")
    @SuppressWarnings("unchecked")
    void merge_Success() {
        when(filmRepository.findAllById(any())).thenReturn(List.of(new FilmEntity(), new FilmEntity()));
        merger.mergeForCreate(entity, dto);
        verify(entity).setName("Coll");
        verify(entity).setDescription("Desc");
        verify(entity).setFilms(any(HashSet.class));
    }

    @Test
    @DisplayName("Should throw exception if any film ID is invalid")
    void merge_FilmsMissing() {
        when(filmRepository.findAllById(any())).thenReturn(List.of(new FilmEntity()));
        assertThrows(ValidationException.class, () -> merger.mergeForUpdate(entity, dto));
    }
}