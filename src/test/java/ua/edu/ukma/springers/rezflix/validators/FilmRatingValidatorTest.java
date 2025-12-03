package ua.edu.ukma.springers.rezflix.validators;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmRatingValidatorTest {

    @Mock private FilmRepository filmRepository;
    @Mock private UserRepository userRepository;
    @Mock private Validator validator;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) private FilmRatingEntity filmRatingEntity;
    private FilmRatingValidator testingValidator;

    @BeforeEach
    void setUp() {
        testingValidator = new FilmRatingValidator(filmRepository, userRepository);
        testingValidator.setValidator(validator);
        when(validator.validate(filmRatingEntity)).thenReturn(Collections.emptySet());
        when(filmRatingEntity.getId().getFilmId()).thenReturn(1);
    }

    @Test
    @DisplayName("Should pass validation when Film and User exist")
    void validForCreate_Success() {
        when(filmRatingEntity.getId().getUserId()).thenReturn(100);
        when(filmRepository.existsById(1)).thenReturn(true);
        when(userRepository.existsById(100)).thenReturn(true);
        assertDoesNotThrow(() -> testingValidator.validForCreate(filmRatingEntity));
    }

    @Test
    @DisplayName("Should throw exception when Film does not exist")
    void validForCreate_FilmNotFound() {
        when(filmRepository.existsById(1)).thenReturn(false);
        assertThrows(ValidationException.class, () -> testingValidator.validForCreate(filmRatingEntity));
    }

    @Test
    @DisplayName("Should throw exception when User does not exist")
    void validForCreate_UserNotFound() {
        when(filmRatingEntity.getId().getUserId()).thenReturn(100);
        when(filmRepository.existsById(1)).thenReturn(true);
        when(userRepository.existsById(100)).thenReturn(false);
        assertThrows(ValidationException.class, () -> testingValidator.validForCreate(filmRatingEntity));
    }
}