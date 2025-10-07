package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmCollectionControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.services.FilmCollectionService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmCollectionController implements FilmCollectionControllerApi {

    private final FilmCollectionService service;

    @Override
    public ResponseEntity<Void> addFilmToCollection(Integer collectionId, Integer filmId) {
        log.info("Adding film {} to collection {}", filmId, collectionId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> clearCollection(Integer collectionId) {
        log.info("Clearing all films from collection {}", collectionId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> removeFilmFromCollection(Integer collectionId, Integer filmId) {
        log.info("Removing film {} from collection {}", filmId, collectionId);
        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<Integer> createFilmCollection(UpsertFilmCollectionDto dto) {
        log.info("Create film collection {}", dto.getName());
        return ResponseEntity.ok(1);
    }

    @Override
    public ResponseEntity<FilmCollectionDto> getFilmCollection(Integer collectionId) {
        return ResponseEntity.ok(new FilmCollectionDto("My Favorites"));
    }

    @Override
    public ResponseEntity<FilmCollectionListDto> getFilmCollectionsByCriteria(FilmCollectionCriteriaDto criteria) {
        log.info("Fetching film collections by criteria: {}", criteria);
        return ResponseEntity.ok(new FilmCollectionListDto(List.of(
                new FilmCollectionDto("Slyga Naroda"),
                new FilmCollectionDto("Slyga Naroda2")
        ), 2L));
    }

    @Override
    public ResponseEntity<Void> updateFilmCollection(Integer collectionId, UpsertFilmCollectionDto dto) {
        log.info("Update film collection {} id={}", dto.getName(), collectionId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteFilmCollection(Integer collectionId) {
        log.info("Delete film collection {}", collectionId);
        return ResponseEntity.noContent().build();
    }
}
