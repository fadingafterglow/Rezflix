package ua.edu.ukma.springers.rezflix.film;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;
import ua.edu.ukma.springers.rezflix.validators.FilmValidator;

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

        when(validator.validate(film)).thenReturn(Set.of());
        when(filmRepository.findIdByTitle(film.getTitle())).thenReturn(Optional.of(99));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> filmValidator.validForCreate(film)
        );
        assertEquals("error.film.title.duplicate", ex.getMessage());
        verify(filmRepository, times(1)).findIdByTitle(film.getTitle());
    }

    @Test
    void validForCreate_shouldNotThrow_whenTitleIsNew() {
        FilmEntity film = new FilmEntity();
        film.setTitle("Fight club");

        when(validator.validate(film)).thenReturn(Set.of());
        when(filmRepository.findIdByTitle(film.getTitle())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> filmValidator.validForCreate(film));
        verify(filmRepository, times(1)).findIdByTitle(film.getTitle());
    }

    @Test
    @SuppressWarnings("unchecked")
    void validForCreate_shouldThrowException_whenConstraintIsViolated() {
        FilmEntity film = new FilmEntity();
        film.setTitle("Sayonara no Asa ni Yakusoku no Hana wo Kazarou");

        when(validator.validate(film)).thenReturn(Set.of(mock(ConstraintViolation.class)));

        assertThrows(
                ValidationException.class,
                () -> filmValidator.validForCreate(film)
        );
        verify(filmRepository, never()).findIdByTitle(film.getTitle());
    }

    @Test
    void validForUpdate_shouldNotThrow_whenTitleIsNotChanged() {
        FilmEntity film = new FilmEntity();
        film.setId(42);
        film.setTitle("Sayonara no Asa ni Yakusoku no Hana wo Kazarou");

        when(validator.validate(film)).thenReturn(Set.of());
        when(filmRepository.findIdByTitle(film.getTitle())).thenReturn(Optional.of(film.getId()));

        assertDoesNotThrow(() -> filmValidator.validForUpdate(film));
        verify(filmRepository, times(1)).findIdByTitle(film.getTitle());
    }
}