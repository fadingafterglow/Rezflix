package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmCollectionControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.services.FilmCollectionService;
import ua.edu.ukma.springers.rezflix.utils.SecurityUtils;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmCollectionController implements FilmCollectionControllerApi {

    private final FilmCollectionService service;
    private final SecurityUtils securityUtils;

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
        log.info("Create film collection {} by user {}", upsertFilmCollectionDto, securityUtils.getCurrentUserId());
        return ResponseEntity.ok(service.create(upsertFilmCollectionDto));
    }

    @Override
    public ResponseEntity<Void> updateFilmCollection(Integer collectionId, UpsertFilmCollectionDto upsertFilmCollectionDto) {
        log.info("Update film collection {} {} by user {}", collectionId, upsertFilmCollectionDto, securityUtils.getCurrentUserId());
        service.update(collectionId, upsertFilmCollectionDto);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteFilmCollection(Integer collectionId) {
        log.info("Delete film collection {} by user {}", collectionId, securityUtils.getCurrentUserId());
        service.delete(collectionId);
        return ResponseEntity.noContent().build();
    }
}
