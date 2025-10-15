package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmCollectionControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.services.FilmCollectionService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmCollectionController implements FilmCollectionControllerApi {

    private final FilmCollectionService service;

    @Override
    public ResponseEntity<FilmCollectionDto> getFilmCollection(Integer collectionId) {
        return ResponseEntity.ok(service.getResponseById(collectionId));
    }

    @Override
    public ResponseEntity<FilmCollectionListDto> getFilmCollectionsByCriteria(FilmCollectionCriteriaDto criteria) {
        return ResponseEntity.ok(service.getListResponseByCriteria(criteria));
    }

    @Override
    public ResponseEntity<Integer> createFilmCollection(UpsertFilmCollectionDto upsertFilmCollectionDto) {
        log.info("Create film collection {}", upsertFilmCollectionDto);
        return ResponseEntity.ok(service.create(upsertFilmCollectionDto));
    }

    @Override
    public ResponseEntity<Void> updateFilmCollection(Integer collectionId, UpsertFilmCollectionDto upsertFilmCollectionDto) {
        log.info("Update film collection {} {}", collectionId, upsertFilmCollectionDto);
        service.update(collectionId, upsertFilmCollectionDto);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteFilmCollection(Integer collectionId) {
        log.info("Delete film collection {}", collectionId);
        service.delete(collectionId);
        return ResponseEntity.noContent().build();
    }
}
