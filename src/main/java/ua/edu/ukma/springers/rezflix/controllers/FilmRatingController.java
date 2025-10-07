package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmRatingControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmRatingDto;
import ua.edu.ukma.springers.rezflix.services.FilmRatingService;
import ua.edu.ukma.springers.rezflix.utils.SecurityUtils;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmRatingController implements FilmRatingControllerApi {

    private final FilmRatingService service;
    private final SecurityUtils securityUtils;

    @Override
    public ResponseEntity<FilmRatingDto> getUserRating(Integer filmId) {
        int userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(service.getUserRatingForFilm(userId, filmId));
    }

    @Override
    public ResponseEntity<Void> setUserRating(Integer filmId, FilmRatingDto dto) {
        int userId = securityUtils.getCurrentUserId();
        log.info("Set user {} rating {} for film {}", userId, dto, filmId);
        service.setUserRatingForFilm(userId, filmId, dto);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteUserRating(Integer filmId) {
        int userId = securityUtils.getCurrentUserId();
        log.info("Delete user {} rating for film {}", userId, filmId);
        service.deleteUserRatingForFilm(userId, filmId);
        return ResponseEntity.noContent().build();
    }
}
