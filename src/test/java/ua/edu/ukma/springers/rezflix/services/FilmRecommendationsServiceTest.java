package ua.edu.ukma.springers.rezflix.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.data.domain.Pageable;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRecommendationEntity;
import ua.edu.ukma.springers.rezflix.mappers.FilmMapper;
import ua.edu.ukma.springers.rezflix.repositories.FilmRatingRepository;
import ua.edu.ukma.springers.rezflix.repositories.FilmRecommendationsRepository;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmRecommendationsServiceTest {

    @Mock private FilmRepository filmRepository;
    @Mock private FilmRatingRepository ratingRepository;
    @Mock private FilmRecommendationsRepository recommendationRepository;
    @Mock private ChatModel chatModel;
    @Mock private ObjectMapper objectMapper;
    @Mock private FilmMapper filmMapper;

    @InjectMocks private FilmRecommendationsService service;

    @Test
    @DisplayName("Should return empty recommendations if user has no ratings")
    void generateRecommendationsAsync_NoRatings() {
        when(ratingRepository.findLastRatingsFetchFilm(anyInt(), any(Pageable.class))).thenReturn(Collections.emptyList());
        service.generateRecommendationsAsync(1);
        verify(chatModel, never()).call(anyString());
    }

    @Test
    @DisplayName("Should return stored recommendations")
    void getRecommendationsForUser() {
        FilmEntity film = new FilmEntity();
        FilmRecommendationEntity rec = new FilmRecommendationEntity(1, 100);
        rec.setFilm(film);

        when(recommendationRepository.findRecommendedFilmsByUserId(1)).thenReturn(List.of(film));
        when(filmMapper.toResponse(eq(film), any())).thenReturn(new FilmDto());

        List<FilmDto> result = service.getRecommendationsForUser(1);
        assertEquals(1, result.size());
    }
}