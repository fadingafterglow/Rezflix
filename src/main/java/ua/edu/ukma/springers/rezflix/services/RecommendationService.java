package ua.edu.ukma.springers.rezflix.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCriteriaDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmListDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRecommendationEntity;
import ua.edu.ukma.springers.rezflix.mappers.FilmMapper;
import ua.edu.ukma.springers.rezflix.repositories.FilmRatingRepository;
import ua.edu.ukma.springers.rezflix.repositories.FilmRecommendationRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final FilmRatingRepository ratingRepository;
    private final FilmRecommendationRepository recommendationRepository;
    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;
    private final FilmService filmService;
    private final FilmMapper filmMapper;

    @Transactional(readOnly = true)
    public FilmListDto getRecommendationsForUser(Integer userId) {
        var entities = recommendationRepository.findAllByUserId(userId);

        if (entities.isEmpty()) {
            return new FilmListDto(Collections.emptyList(), 0L);
        }

        var films = entities.stream()
                .map(e -> filmService.getById(e.getFilmId()))
                .filter(Objects::nonNull)
                .map(f -> filmMapper.toResponse(f, Collections.emptyMap()))
                .toList();

        return new FilmListDto(films, (long) films.size());
    }

    @Async
    @Transactional
    public void generateRecommendationsAsync(Integer userId) {
        log.info("Starting async recommendation generation for user {}", userId);

        var lastRatings = ratingRepository.findLastRatings(userId, PageRequest.of(0, 10));
        if (lastRatings.isEmpty()) {
            log.info("User {} has no ratings -> skip generation");
            return;
        }

        var titles = requestAiRecommendations(lastRatings);
        if (titles.isEmpty()) return;

        var resolved = resolveMovies(titles);
        var filtered = filterAlreadyRated(userId, resolved);

        if (filtered.isEmpty()) {
            log.info("All recommended films were already rated by user {}", userId);
            return;
        }

        saveRecommendations(userId, filtered);
    }

    private List<String> requestAiRecommendations(List<FilmRatingEntity> lastRatings) {
        var prompt = buildPrompt(lastRatings);
        var raw = chatModel.call(prompt);
        return parseTitles(raw);
    }

    private List<FilmDto> resolveMovies(List<String> titles) {
        return titles.stream().map(title -> {
                    try {
                        var criteria = new FilmCriteriaDto().query(title);
                        var list = filmService.getListResponseByCriteria(criteria);

                        return (list != null && !list.getItems().isEmpty())
                                ? list.getItems().get(0)
                                : null;

                    } catch (Exception e) {
                        log.warn("Error resolving film '{}': {}", title, e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private List<FilmDto> filterAlreadyRated(Integer userId, List<FilmDto> films) {
        return films.stream()
                .filter(f -> !ratingRepository.existsByUserIdAndFilmId(userId, f.getId()))
                .toList();
    }

    private void saveRecommendations(Integer userId, List<FilmDto> films) {
        recommendationRepository.deleteByUserId(userId);

        var entities = films.stream()
                .map(f -> new FilmRecommendationEntity(userId, f.getId()))
                .toList();

        recommendationRepository.saveAll(entities);
    }

    private List<String> parseTitles(String rawJson) {
        try {
            String clean = sanitizeJson(rawJson);

            JsonNode root = objectMapper.readTree(clean);

            if (!root.has("recommendations")) return Collections.emptyList();

            JsonNode arr = root.get("recommendations");
            if (!arr.isArray()) return Collections.emptyList();

            List<String> titles = new ArrayList<>();
            for (JsonNode n : arr) {
                if (n.isTextual()) titles.add(n.asText());
            }
            return titles;

        } catch (JsonProcessingException e) {
            log.warn("Failed to parse AI JSON: {}", rawJson);
            return Collections.emptyList();
        }
    }

    private String sanitizeJson(String raw) {
        if (raw == null) return "{}";
        return raw.replace("```json", "")
                .replace("```", "")
                .trim();
    }

    private String buildPrompt(List<FilmRatingEntity> ratings) {
        var sb = new StringBuilder("""
            You are a movie recommendation expert for Rezflix.
            Based on the user's last movie ratings, suggest 10 movies they would like.

            RULES:
            - Return ONLY valid JSON.
            - NO markdown formatting.
            - "recommendations" MUST be an array of strings.
            - Titles must be exact English movie titles.
            - Do NOT recommend movies the user has already rated.

            Example:
            {
              "recommendations": [
                "The Matrix",
                "Inception"
              ]
            }

            User History:
            """);

        for (var r : ratings) {
            var film = r.getFilm();
            var title = film != null ? film.getTitle() : "Unknown";
            var desc = film != null ? film.getDescription() : "";

            if (desc.length() > 100) desc = desc.substring(0, 100) + "...";

            sb.append("- Title: \"%s\", Rating: %d/10, Context: \"%s\"%n"
                    .formatted(title, r.getRating(), desc));
        }

        return sb.toString();
    }
}