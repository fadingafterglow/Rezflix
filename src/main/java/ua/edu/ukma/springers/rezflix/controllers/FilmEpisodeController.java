package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmEpisodeControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.services.FilmEpisodeService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmEpisodeController implements FilmEpisodeControllerApi {

    private final FilmEpisodeService service;

    @Override
    public ResponseEntity<EpisodeDto> getEpisode(UUID episodeId) {
        return ResponseEntity.ok(service.getResponseById(episodeId));
    }

    @Override
    public ResponseEntity<EpisodeListDto> getEpisodesByCriteria(Integer dubbingId, EpisodeCriteriaDto criteria) {
        return ResponseEntity.ok(service.getListResponseByCriteria(dubbingId, criteria));
    }

    @Override
    public ResponseEntity<UUID> createEpisode(Integer dubbingId, UpdateEpisodeDto metadata, InputStreamResource file) {
        log.info("Creating episode {} for dubbing {}", metadata, dubbingId);
        return ResponseEntity.ok(service.create(new CreateEpisodeDto(dubbingId, metadata, file)));
    }

    @Override
    public ResponseEntity<Void> updateEpisodeMetadata(UUID episodeId, UpdateEpisodeDto updateEpisodeDto) {
        log.info("Updating episode {} {}", episodeId, updateEpisodeDto);
        service.update(episodeId, updateEpisodeDto);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteEpisode(UUID episodeId) {
        log.info("Deleting episode {}", episodeId);
        service.delete(episodeId);
        return ResponseEntity.noContent().build();
    }
}
