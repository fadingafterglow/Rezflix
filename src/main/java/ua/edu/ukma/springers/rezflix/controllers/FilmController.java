package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.services.FilmService;
import ua.edu.ukma.springers.rezflix.utils.SecurityUtils;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmController implements FilmControllerApi {

    private final FilmService service;
    private final SecurityUtils securityUtils;

    @Override
    public ResponseEntity<FilmDto> getFilm(Integer filmId) {
        return ResponseEntity.ok(service.getResponseById(filmId));
    }

    @Override
    public ResponseEntity<FilmListDto> getFilmsByCriteria(FilmCriteriaDto criteria) {
        return ResponseEntity.ok(service.getListResponseByCriteria(criteria));
    }

    @Override
    public ResponseEntity<Integer> createFilm(UpsertFilmDto dto) {
        log.info("Create film {} by user {}", dto, securityUtils.getCurrentUserId());
        return ResponseEntity.ok(service.create(dto));
    }

    @Override
    public ResponseEntity<Void> updateFilm(Integer filmId, UpsertFilmDto dto) {
        log.info("Update film {} {} by user {}", filmId, dto, securityUtils.getCurrentUserId());
        service.update(filmId, dto);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteFilm(Integer filmId) {
        log.info("Delete film {} by user {}", filmId, securityUtils.getCurrentUserId());
        service.delete(filmId);
        return ResponseEntity.noContent().build();
    }
}
