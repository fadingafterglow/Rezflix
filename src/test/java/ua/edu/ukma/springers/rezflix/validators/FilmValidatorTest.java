package ua.edu.ukma.springers.rezflix.validators;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmValidatorTest {

    @Mock private FilmRepository filmRepository;
    @Mock private Validator validator;
    @Mock private FilmEntity filmEntity;
    private FilmValidator testingValidator;

    @BeforeEach
    void setUp() {
        testingValidator = new FilmValidator(filmRepository);
        testingValidator.setValidator(validator);
        when(validator.validate(filmEntity)).thenReturn(Collections.emptySet());
        when(filmEntity.getTitle()).thenReturn("Title");
    }

    @Test
    @DisplayName("Should create successfully when title is unique")
    void validForCreate_UniqueTitle() {
        when(filmRepository.findIdByTitle("Title")).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> testingValidator.validForCreate(filmEntity));
    }

    @Test
    @DisplayName("Should throw exception on create when title is duplicate")
    void validForCreate_DuplicateTitle() {
        when(filmRepository.findIdByTitle("Title")).thenReturn(Optional.of(5));
        assertThrows(ValidationException.class, () -> testingValidator.validForCreate(filmEntity));
    }

    @Test
    @DisplayName("Should update successfully when title belongs to the same entity")
    void validForUpdate_SameEntityTitle() {
        when(filmEntity.getId()).thenReturn(10);
        when(filmRepository.findIdByTitle("Title")).thenReturn(Optional.of(10));
        assertDoesNotThrow(() -> testingValidator.validForUpdate(filmEntity));
    }

    @Test
    @DisplayName("Should throw exception on update when title belongs to another entity")
    void validForUpdate_DuplicateTitleDifferentEntity() {
        when(filmEntity.getId()).thenReturn(10);
        when(filmRepository.findIdByTitle("Title")).thenReturn(Optional.of(20));
        assertThrows(ValidationException.class, () -> testingValidator.validForUpdate(filmEntity));
    }
}