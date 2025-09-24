package ua.edu.ukma.springers.rezflix.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmInfoLookupControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmInfoLookupResultDto;
import ua.edu.ukma.springers.rezflix.services.FilmInfoLookupService;

@RestController
public class FilmInfoLookupController implements FilmInfoLookupControllerApi {

    @Autowired
    private FilmInfoLookupService service;

    @Override
    public ResponseEntity<FilmInfoLookupResultDto> lookupFilmInfo(String title) {
        return ResponseEntity.ok(service.lookupFilmInfo(title));
    }
}
