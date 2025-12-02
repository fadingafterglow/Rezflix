package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmRecommendationsControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmDto;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;
import ua.edu.ukma.springers.rezflix.services.FilmRecommendationsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FilmRecommendationsController implements FilmRecommendationsControllerApi {

    private final FilmRecommendationsService service;
    private final SecurityUtils securityUtils;

    @Override
    public ResponseEntity<List<FilmDto>> getRecommendations() {
        int userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(service.getRecommendationsForUser(userId));
    }

}