package ua.edu.ukma.springers.rezflix.validators;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmDubbingEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmDubbingRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmDubbingValidatorTest {

    @Mock private FilmDubbingRepository repository;
    @Mock private Validator validator;
    @InjectMocks private FilmDubbingValidator testingValidator;

    @BeforeEach
    void setUp() {
        testingValidator.setValidator(validator);
    }

    @Test
    void validForCreate_UniqueName() {
        FilmDubbingEntity entity = new FilmDubbingEntity();
        entity.setFilmId(1);
        entity.setName("Unique");

        when(validator.validate(entity)).thenReturn(Collections.emptySet());
        when(repository.findIdByFilmIdAndName(1, "Unique")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> testingValidator.validForCreate(entity));
    }

    @Test
    void validForCreate_DuplicateName() {
        FilmDubbingEntity entity = new FilmDubbingEntity();
        entity.setFilmId(1);
        entity.setName("Duplicate");

        when(validator.validate(entity)).thenReturn(Collections.emptySet());
        when(repository.findIdByFilmIdAndName(1, "Duplicate")).thenReturn(Optional.of(100));

        assertThrows(ValidationException.class, () -> testingValidator.validForCreate(entity));
    }
}