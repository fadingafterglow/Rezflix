package ua.edu.ukma.springers.rezflix.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.util.unit.DataSize;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpdateEpisodeDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEpisodeEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.FilmEpisodeStatus;
import ua.edu.ukma.springers.rezflix.mappers.EnumsMapper;
import ua.edu.ukma.springers.rezflix.mappers.FilmEpisodeMapper;
import ua.edu.ukma.springers.rezflix.repositories.FilmEpisodeRepository;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;
import ua.edu.ukma.springers.rezflix.services.rendering.RenderingService;

import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FilmEpisodeServiceTest extends BaseServiceTest<FilmEpisodeService, FilmEpisodeEntity, CreateEpisodeDto, UpdateEpisodeDto, UUID> {

    @Mock private FilmEpisodeRepository episodeRepository;
    @Mock private FilmEpisodeMapper mapper;
    @Mock private EnumsMapper enumsMapper;
    @Mock private RenderingService renderingService;
    @Mock private SecurityUtils securityUtils;

    @TempDir Path tempDir;

    @Override
    protected FilmEpisodeService createService() {
        this.repository = episodeRepository;
        return new FilmEpisodeService(mapper, enumsMapper, securityUtils, renderingService, tempDir, DataSize.ofMegabytes(10));
    }

    @Test
    @DisplayName("Should create episode, save file and launch rendering")
    void createEntity() {
        UUID id = UUID.randomUUID();
        CreateEpisodeDto createDto = new CreateEpisodeDto(
                1,
                new UpdateEpisodeDto(),
                new InputStreamResource(new ByteArrayResource(new byte[]{1, 2, 3}))
        );

        when(repository.save(any(FilmEpisodeEntity.class))).thenAnswer(inv -> {
            FilmEpisodeEntity e = inv.getArgument(0);
            e.setId(id);
            return e;
        });

        FilmEpisodeEntity result = service.createEntity(null, createDto);

        assertNotNull(result);
        assertEquals(FilmEpisodeStatus.BEING_RENDERED, result.getStatus());
        verify(renderingService).launchRenderingJob(eq(id), any(Path.class));
    }

    @Test
    @DisplayName("Should delete episode and cleanup files")
    void deleteEntity() {
        UUID id = UUID.randomUUID();
        FilmEpisodeEntity entity = new FilmEpisodeEntity();
        entity.setId(id);

        when(repository.findById(id)).thenReturn(java.util.Optional.of(entity));

        service.delete(id);

        verify(repository).delete(entity);
        verify(renderingService).cleanupRenderingFiles(id);
    }
}