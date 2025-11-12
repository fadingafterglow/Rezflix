package ua.edu.ukma.springers.rezflix;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;
import ua.edu.ukma.springers.rezflix.validators.FilmValidator;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmValidatorTest {

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private Validator validator;

    @Mock
    private Set<ConstraintViolation> violations;

    @InjectMocks
    private FilmValidator filmValidator;

    @BeforeEach
    void setUp() {
        filmValidator.setValidator(validator);
    }

    @Test
    void validForCreate_shouldThrowException_whenTitleAlreadyExists() {
        FilmEntity film = new FilmEntity();
        film.setTitle("ChainsawMan");

        when(validator.validate(film)).thenReturn(Collections.emptySet());

        when(filmRepository.findIdByTitle("ChainsawMan")).thenReturn(Optional.of(99));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> filmValidator.validForCreate(film)
        );

        assertEquals("error.film.title.duplicate", ex.getMessage());
        verify(filmRepository, times(1)).findIdByTitle("ChainsawMan");
    }

    @Test
    void validForCreate_shouldNotThrow_whenTitleIsNew() {
        FilmEntity film = new FilmEntity();
        film.setTitle("Fight club");

        when(validator.validate(film)).thenReturn(Collections.emptySet());

        when(filmRepository.findIdByTitle("Fight club")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> filmValidator.validForCreate(film));
        verify(filmRepository, times(1)).findIdByTitle("Fight club");
    }

    @Test
    @SuppressWarnings("unchecked")
    void validForCreate_shouldReturnConstraintViolation() {
        FilmEntity film = new FilmEntity();
        film.setTitle("Sayonara no Asa ni Yakusoku no Hana wo Kazarou");

        when(validator.validate(film)).thenReturn(Set.of(Mockito.mock(ConstraintViolation.class)));

        assertThrows(
                ValidationException.class,
                () -> filmValidator.validForCreate(film)
        );

        verify(filmRepository, never()).findIdByTitle("Sayonara no Asa ni Yakusoku no Hana wo Kazarou");
    }
}