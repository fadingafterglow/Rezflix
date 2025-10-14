package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmInfoLookupControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmInfoLookupResultDto;
import ua.edu.ukma.springers.rezflix.services.FilmInfoLookupService;

@RestController
@RequiredArgsConstructor
@ConditionalOnExpression("${api.film-info.enable:true}")
public class FilmInfoLookupController implements FilmInfoLookupControllerApi {

    private final FilmInfoLookupService service;

    @Override
    public ResponseEntity<FilmInfoLookupResultDto> lookupFilmInfo(String title) {
        return ResponseEntity.ok(service.lookupFilmInfo(title));
    }
}
