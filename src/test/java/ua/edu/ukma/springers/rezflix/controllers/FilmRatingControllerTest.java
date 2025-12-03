package ua.edu.ukma.springers.rezflix.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmRatingDto;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;
import ua.edu.ukma.springers.rezflix.services.FilmRatingService;
import ua.edu.ukma.springers.rezflix.services.FilmRecommendationsService;
import ua.edu.ukma.springers.rezflix.utils.ApiPaths;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmRatingController.class)
class FilmRatingControllerTest extends BaseControllerTest {

    @MockitoBean
    private FilmRatingService ratingService;
    @MockitoBean
    private FilmRecommendationsService recommendationService;

    private String viewerToken;
    private final int VIEWER_ID = 10;
    private final int FILM_ID = 55;

    @BeforeEach
    void setUp() {
        String viewerUsername = "viewer";
        UserEntity viewer = new UserEntity();
        viewer.setId(VIEWER_ID);
        viewer.setUsername(viewerUsername);
        viewer.setPasswordHash(passwordEncoder.encode("password"));
        viewer.setType(UserType.VIEWER);

        when(userRepository.findByUsername(viewerUsername)).thenReturn(Optional.of(viewer));

        this.viewerToken = "Bearer " + jwtService.generateAccessToken(viewerUsername, UserRole.VIEWER.name());
    }

    @Test
    void getUserRating_Viewer_ReturnsRating() throws Exception {
        FilmRatingDto ratingDto = new FilmRatingDto(8);

        when(ratingService.getUserRatingForFilm(VIEWER_ID, FILM_ID)).thenReturn(ratingDto);

        mvc.perform(get(ApiPaths.FILM_RATING_API.BASE, FILM_ID)
                        .header(HttpHeaders.AUTHORIZATION, viewerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(8));
    }

    @Test
    void setUserRating_Viewer_UpdatesAndTriggersRecommendation() throws Exception {
        FilmRatingDto ratingDto = new FilmRatingDto(5);

        doNothing().when(ratingService).setUserRatingForFilm(eq(VIEWER_ID), eq(FILM_ID), any(FilmRatingDto.class));
        doNothing().when(recommendationService).generateRecommendationsAsync(VIEWER_ID);

        mvc.perform(put(ApiPaths.FILM_RATING_API.BASE, FILM_ID)
                        .header(HttpHeaders.AUTHORIZATION, viewerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingDto)))
                .andExpect(status().isNoContent());

        verify(recommendationService, times(1)).generateRecommendationsAsync(VIEWER_ID);
        verify(ratingService, times(1)).setUserRatingForFilm(eq(VIEWER_ID), eq(FILM_ID), any(FilmRatingDto.class));
    }

    @Test
    void deleteUserRating_Viewer_DeletesAndTriggersRecommendation() throws Exception {
        doNothing().when(ratingService).deleteUserRatingForFilm(VIEWER_ID, FILM_ID);
        doNothing().when(recommendationService).generateRecommendationsAsync(VIEWER_ID);

        mvc.perform(delete(ApiPaths.FILM_RATING_API.BASE, FILM_ID)
                        .header(HttpHeaders.AUTHORIZATION, viewerToken))
                .andExpect(status().isNoContent());

        verify(recommendationService, times(1)).generateRecommendationsAsync(VIEWER_ID);
        verify(ratingService, times(1)).deleteUserRatingForFilm(VIEWER_ID, FILM_ID);
    }

    @Test
    void setUserRating_Anonymous_ReturnsUnauthorized() throws Exception {
        FilmRatingDto ratingDto = new FilmRatingDto(5);

        mvc.perform(put(ApiPaths.FILM_RATING_API.BASE, FILM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingDto)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(ratingService);
        verifyNoInteractions(recommendationService);
    }
}