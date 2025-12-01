package ua.edu.ukma.springers.rezflix.mergers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpsertFilmDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmMergerTest {

    @Mock private FilmEntity entity;
    @Mock private UpsertFilmDto dto;
    private FilmMerger merger;

    @BeforeEach
    void setUp() {
        merger = new FilmMerger();
        when(dto.getTitle()).thenReturn("New Title");
        when(dto.getDescription()).thenReturn("New Desc");
    }

    @Test
    @DisplayName("Should merge fields for create")
    void mergeForCreate() {
        merger.mergeForCreate(entity, dto);
        verify(entity).setTitle("New Title");
        verify(entity).setDescription("New Desc");
    }

    @Test
    @DisplayName("Should merge fields for update")
    void mergeForUpdate() {
        merger.mergeForUpdate(entity, dto);
        verify(entity).setTitle("New Title");
        verify(entity).setDescription("New Desc");
    }
}