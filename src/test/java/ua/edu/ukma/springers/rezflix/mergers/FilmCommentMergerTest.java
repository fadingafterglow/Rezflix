package ua.edu.ukma.springers.rezflix.mergers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateCommentDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpdateCommentDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCommentEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;
import ua.edu.ukma.springers.rezflix.utils.TimeUtils;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmCommentMergerTest {

    @Mock private FilmRepository filmRepo;
    @Mock private FilmCommentEntity entity;
    @Mock private FilmEntity filmEntity;

    private FilmCommentMerger merger;
    private CreateCommentDto createDto;
    private UpdateCommentDto updateDto;

    @BeforeEach
    void setUp() {
        merger = new FilmCommentMerger(filmRepo);

        createDto = new CreateCommentDto();
        createDto.setFilmId(1);
        createDto.setText("Comment");

        updateDto = new UpdateCommentDto();
        updateDto.setText("Updated");
    }

    @Test
    @DisplayName("Should set film, text and time for create")
    void mergeForCreate_Success() {
        when(filmRepo.findById(1)).thenReturn(Optional.of(filmEntity));

        try (MockedStatic<TimeUtils> timeUtils = mockStatic(TimeUtils.class)) {
            timeUtils.when(TimeUtils::getCurrentDateTimeUTC).thenReturn(LocalDateTime.MIN);
            merger.mergeForCreate(entity, createDto);

            verify(entity).setFilm(filmEntity);
            verify(entity).setText("Comment");
            verify(entity).setCreatedAt(LocalDateTime.MIN);
        }
    }

    @Test
    @DisplayName("Should throw ValidationException if film not found during create")
    void mergeForCreate_FilmNotFound() {
        createDto.setFilmId(999);
        when(filmRepo.findById(999)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> merger.mergeForCreate(entity, createDto));
    }

    @Test
    @DisplayName("Should update text only")
    void mergeForUpdate() {
        merger.mergeForUpdate(entity, updateDto);
        verify(entity).setText("Updated");
    }
}