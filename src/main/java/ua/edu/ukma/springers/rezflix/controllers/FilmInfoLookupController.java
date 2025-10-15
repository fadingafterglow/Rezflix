package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmInfoLookupControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmInfoLookupResultDto;
import ua.edu.ukma.springers.rezflix.logging.Markers;
import ua.edu.ukma.springers.rezflix.services.FilmInfoLookupService;

@Slf4j
@RestController
@ConditionalOnBean(FilmInfoLookupService.class)
@RequiredArgsConstructor
public class FilmInfoLookupController implements FilmInfoLookupControllerApi {

    private final FilmInfoLookupService service;

    @Override
    public ResponseEntity<FilmInfoLookupResultDto> lookupFilmInfo(String title) {
        log.info(Markers.LOCAL, "Lookup film info for title: {}", title);
        return ResponseEntity.ok(service.lookupFilmInfo(title));
    }
}
