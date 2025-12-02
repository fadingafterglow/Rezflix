package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmDubbingControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.services.FilmDubbingService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmDubbingController implements FilmDubbingControllerApi {

    private final FilmDubbingService service;

    @Override
    public ResponseEntity<DubbingDto> getDubbing(Integer dubbingId) {
        return ResponseEntity.ok(service.getResponseById(dubbingId));
    }

    @Override
    public ResponseEntity<DubbingListDto> getDubbingsByCriteria(DubbingCriteriaDto criteria) {
        return ResponseEntity.ok(service.getListResponseByCriteria(criteria));
    }

    @Override
    public ResponseEntity<Integer> createDubbing(CreateDubbingDto createDubbingDto) {
        log.info("Creating dubbing: {}", createDubbingDto);
        return ResponseEntity.ok(service.create(createDubbingDto));
    }

    @Override
    public ResponseEntity<Void> updateDubbing(Integer dubbingId, UpdateDubbingDto updateDubbingDto) {
        log.info("Updating dubbing {} {}", dubbingId, updateDubbingDto);
        service.update(dubbingId, updateDubbingDto);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteDubbing(Integer dubbingId) {
        log.info("Deleting dubbing {}", dubbingId);
        service.delete(dubbingId);
        return ResponseEntity.noContent().build();
    }
}
