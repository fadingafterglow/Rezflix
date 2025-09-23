package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmInfoControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmController implements FilmInfoControllerApi {
    @Override
    public ResponseEntity<Integer> createFilm(CreateFilmDto dto) {
        log.info("Create film {}", dto.getTitle());
        return ResponseEntity.ok(1);
    }

    @Override
    public ResponseEntity<FilmDto> getFilm(Integer filmId) {
        return ResponseEntity.ok(new FilmDto("Slyga Naroda", "Ya lyublyu svoyu stranu"));
    }

    @Override
    public ResponseEntity<FilmsListDto> getFilmsByCriteria(FilmsListCriteriaDto criteria) {
        return ResponseEntity.ok(new FilmsListDto(List.of(
                new FilmDto("Slyga Naroda", "Ya, lyublyu svoyu stranu"),
                new FilmDto("Slyga Naroda", "Lyublyu svoyu zhenu"),
                new FilmDto("Slyga Naroda", "Lyublyu svoyu sobaku"),

                new FilmDto("Slyga Naroda", "Ya, vsego na svete chlen"),
                new FilmDto("Slyga Naroda", "Pochti chto Superman"),
                new FilmDto("Slyga Naroda", "No redko lezu v draku"),
                new FilmDto("Slyga Naroda", "Znayet ves dvor"),
                new FilmDto("Slyga Naroda", "Moy prigovor, sluga narodu"),

                new FilmDto("Slyga Naroda", "U menya pochti vsyo yest'"),
                new FilmDto("Slyga Naroda", "Dostoinstva i chest'"),
                new FilmDto("Slyga Naroda", "I dazhe kriki, bravo"),

                new FilmDto("Slyga Naroda", "Personal'nyy samolet"),
                new FilmDto("Slyga Naroda", "Mne vydelil narod"),
                new FilmDto("Slyga Naroda", "A chto? imeyu pravo"),

                new FilmDto("Slyga Naroda", "Na zhivotu (vot tut)"),
                new FilmDto("Slyga Naroda", "Nabyu tatu, sluga narodu")
        ), 42L));
    }

    @Override
    public ResponseEntity<Void> updateFilm(Integer filmId, UpdateFilmDto dto) {
        log.info("Update film {} by id {}", dto.getTitle(), filmId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteFilm(Integer filmId) {
        log.info("Delete film {}", filmId);
        return ResponseEntity.noContent().build();
    }
}
