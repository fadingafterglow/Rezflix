package ua.edu.ukma.springers.rezflix.mergers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmRatingDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingEntity;
import ua.edu.ukma.springers.rezflix.utils.TimeUtils;
import java.time.LocalDateTime;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FilmRatingMergerTest {

    @Mock private FilmRatingEntity entity;
    private FilmRatingDto dto;
    private FilmRatingMerger merger;

    @BeforeEach
    void setUp() {
        merger = new FilmRatingMerger();
        dto = new FilmRatingDto();
        dto.setRating(5);
    }

    @Test
    @DisplayName("Should merge rating and set created time for create")
    void mergeForCreate() {
        try (MockedStatic<TimeUtils> timeUtils = mockStatic(TimeUtils.class)) {
            timeUtils.when(TimeUtils::getCurrentDateTimeUTC).thenReturn(LocalDateTime.MIN);
            merger.mergeForCreate(entity, dto);
            verify(entity).setRating(5);
            verify(entity).setCreatedAt(LocalDateTime.MIN);
        }
    }

    @Test
    @DisplayName("Should merge rating and set updated time for update")
    void mergeForUpdate() {
        try (MockedStatic<TimeUtils> timeUtils = mockStatic(TimeUtils.class)) {
            timeUtils.when(TimeUtils::getCurrentDateTimeUTC).thenReturn(LocalDateTime.MAX);
            merger.mergeForUpdate(entity, dto);
            verify(entity).setRating(5);
            verify(entity).setUpdatedAt(LocalDateTime.MAX);
        }
    }
}