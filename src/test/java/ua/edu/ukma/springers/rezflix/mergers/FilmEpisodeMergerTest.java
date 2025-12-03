package ua.edu.ukma.springers.rezflix.mergers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateEpisodeDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpdateEpisodeDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmDubbingEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEpisodeEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmDubbingRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmEpisodeMergerTest {

    @Mock private FilmDubbingRepository dubbingRepo;
    @InjectMocks private FilmEpisodeMerger merger;

    @Test
    void mergeForCreate_Success() {
        FilmEpisodeEntity entity = new FilmEpisodeEntity();
        UpdateEpisodeDto meta = new UpdateEpisodeDto();
        meta.setTitle("Ep 1");
        meta.setWatchOrder(1);
        CreateEpisodeDto dto = new CreateEpisodeDto(10, meta, null);

        FilmDubbingEntity dubbing = new FilmDubbingEntity();
        when(dubbingRepo.findById(10)).thenReturn(Optional.of(dubbing));

        merger.mergeForCreate(entity, dto);

        assertEquals(10, entity.getFilmDubbingId());
        assertEquals("Ep 1", entity.getTitle());
        assertEquals(1, entity.getWatchOrder());
        assertEquals(dubbing, entity.getFilmDubbing());
    }

    @Test
    void mergeForCreate_DubbingNotFound() {
        FilmEpisodeEntity entity = new FilmEpisodeEntity();
        CreateEpisodeDto dto = new CreateEpisodeDto(999, new UpdateEpisodeDto(), null);

        when(dubbingRepo.findById(999)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> merger.mergeForCreate(entity, dto));
    }

    @Test
    void mergeForUpdate() {
        FilmEpisodeEntity entity = new FilmEpisodeEntity();
        UpdateEpisodeDto dto = new UpdateEpisodeDto();
        dto.setTitle("Updated");
        dto.setWatchOrder(5);

        merger.mergeForUpdate(entity, dto);

        assertEquals("Updated", entity.getTitle());
        assertEquals(5, entity.getWatchOrder());
    }
}