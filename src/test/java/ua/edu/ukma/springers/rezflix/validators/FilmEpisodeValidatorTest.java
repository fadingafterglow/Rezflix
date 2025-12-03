package ua.edu.ukma.springers.rezflix.validators;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEpisodeEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.FilmEpisodeStatus;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.exceptions.NotFoundException;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmEpisodeRepository;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmEpisodeValidatorTest {

    @Mock private FilmEpisodeRepository repository;
    @Mock private SecurityUtils securityUtils;
    @Mock private Validator validator;
    @InjectMocks private FilmEpisodeValidator testingValidator;

    @BeforeEach
    void setUp() {
        testingValidator.setValidator(validator);
    }

    @Test
    void validForView_NotRendered_NotContentManager() {
        FilmEpisodeEntity entity = new FilmEpisodeEntity();
        entity.setStatus(FilmEpisodeStatus.BEING_RENDERED);

        when(securityUtils.hasRole(UserRole.CONTENT_MANAGER)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> testingValidator.validForView(entity));
    }

    @Test
    void validForView_Rendered_Viewer() {
        FilmEpisodeEntity entity = new FilmEpisodeEntity();
        entity.setStatus(FilmEpisodeStatus.RENDERED);

        assertDoesNotThrow(() -> testingValidator.validForView(entity));
    }

    @Test
    void validForCreate_DuplicateOrder() {
        FilmEpisodeEntity entity = new FilmEpisodeEntity();
        entity.setFilmDubbingId(1);
        entity.setWatchOrder(1);

        when(validator.validate(entity)).thenReturn(Collections.emptySet());
        when(repository.findIdByFilmDubbingIdAndWatchOrder(1, 1)).thenReturn(Optional.of(UUID.randomUUID()));

        assertThrows(ValidationException.class, () -> testingValidator.validForCreate(entity));
    }
}