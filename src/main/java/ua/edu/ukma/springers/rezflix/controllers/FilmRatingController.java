package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmRatingControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.RateFilmDto;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmRatingController implements FilmRatingControllerApi {
    @Override
    public ResponseEntity<Void> deleteUserRating(Integer filmId) {
        log.info("Delete user rating for film id {}", filmId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RateFilmDto> getUserRating(Integer filmId) {
        return ResponseEntity.ok(new RateFilmDto(5));
    }

    @Override
    public ResponseEntity<Void> setUserRating(Integer filmId, RateFilmDto rateFilmDto) {
        log.info("Set user rating for film id {}", filmId);
        return ResponseEntity.noContent().build();
    }
}
