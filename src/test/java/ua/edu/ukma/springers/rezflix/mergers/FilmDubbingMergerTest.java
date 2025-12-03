package ua.edu.ukma.springers.rezflix.mergers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateDubbingDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpdateDubbingDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmDubbingEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmDubbingMergerTest {

    @Mock private FilmRepository filmRepo;
    @InjectMocks private FilmDubbingMerger merger;

    @Test
    void mergeForCreate_Success() {
        FilmDubbingEntity entity = new FilmDubbingEntity();
        CreateDubbingDto dto = new CreateDubbingDto();
        dto.setFilmId(1);
        dto.setName("Ukr Dub");

        FilmEntity film = new FilmEntity();
        when(filmRepo.findById(1)).thenReturn(Optional.of(film));

        merger.mergeForCreate(entity, dto);

        assertEquals(1, entity.getFilmId());
        assertEquals("Ukr Dub", entity.getName());
        assertEquals(film, entity.getFilm());
    }

    @Test
    void mergeForCreate_FilmNotFound() {
        FilmDubbingEntity entity = new FilmDubbingEntity();
        CreateDubbingDto dto = new CreateDubbingDto();
        dto.setFilmId(999);

        when(filmRepo.findById(999)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> merger.mergeForCreate(entity, dto));
    }

    @Test
    void mergeForUpdate() {
        FilmDubbingEntity entity = new FilmDubbingEntity();
        UpdateDubbingDto dto = new UpdateDubbingDto();
        dto.setName("New Name");

        merger.mergeForUpdate(entity, dto);

        assertEquals("New Name", entity.getName());
    }
}