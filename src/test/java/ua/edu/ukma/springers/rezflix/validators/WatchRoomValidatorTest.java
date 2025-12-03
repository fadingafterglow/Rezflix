package ua.edu.ukma.springers.rezflix.validators;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.domain.entities.WatchRoomEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.FilmEpisodeStatus;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmEpisodeRepository;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WatchRoomValidatorTest {

    @Mock private Validator validator;
    @Mock private FilmEpisodeRepository episodeRepository;
    @InjectMocks private WatchRoomValidator testingValidator;

    @Test
    void validForCreate_Success() {
        WatchRoomEntity entity = new WatchRoomEntity();
        entity.setEpisodeId(UUID.randomUUID());

        when(validator.validate(entity)).thenReturn(Collections.emptySet());
        when(episodeRepository.existsByIdAndStatus(entity.getEpisodeId(), FilmEpisodeStatus.RENDERED)).thenReturn(true);

        assertDoesNotThrow(() -> testingValidator.validForCreate(entity));
    }

    @Test
    void validForCreate_EpisodeNotRenderedOrMissing() {
        WatchRoomEntity entity = new WatchRoomEntity();
        entity.setEpisodeId(UUID.randomUUID());

        when(validator.validate(entity)).thenReturn(Collections.emptySet());
        when(episodeRepository.existsByIdAndStatus(entity.getEpisodeId(), FilmEpisodeStatus.RENDERED)).thenReturn(false);

        assertThrows(ValidationException.class, () -> testingValidator.validForCreate(entity));
    }
}