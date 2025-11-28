package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmRecommendationControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmListDto;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;
import ua.edu.ukma.springers.rezflix.services.RecommendationService;


@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmRecommendationController implements FilmRecommendationControllerApi {

    private final RecommendationService recommendationService;
    private final SecurityUtils securityUtils;

    @Override
    public ResponseEntity<FilmListDto> getRecommendations() {
        Integer userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(recommendationService.getRecommendationsForUser(userId));
    }

}