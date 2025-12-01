package ua.edu.ukma.springers.rezflix.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRecommendationEntity;
import ua.edu.ukma.springers.rezflix.mappers.FilmMapper;
import ua.edu.ukma.springers.rezflix.repositories.FilmRatingRepository;
import ua.edu.ukma.springers.rezflix.repositories.FilmRecommendationsRepository;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmRecommendationsService {

    private final FilmRepository filmRepository;
    private final FilmRatingRepository ratingRepository;
    private final FilmRecommendationsRepository recommendationRepository;
    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;
    private final FilmMapper filmMapper;

    @Transactional(readOnly = true)
    public List<FilmDto> getRecommendationsForUser(int userId) {
        return recommendationRepository.findRecommendedFilmsByUserId(userId).stream()
                .map(f -> filmMapper.toResponse(f, Collections.emptyMap()))
                .toList();
    }

    @Async
    @Transactional
    public void generateRecommendationsAsync(int userId) {
        log.info("Started recommendation generation for user {}", userId);

        var lastRatings = ratingRepository.findLastRatingsFetchFilm(userId, PageRequest.of(0, 10));
        if (lastRatings.isEmpty()) {
            log.info("User {} has no ratings. No recommendations will be generated", userId);
            return;
        }

        var titles = requestAiRecommendations(lastRatings);
        if (titles.isEmpty()) {
            log.info("AI returned no recommendations for user {}", userId);
            return;
        }

        log.debug("AI recommendations for user {}: {}", userId, titles);

        var resolved = resolveMovies(userId, titles);
        if (resolved.isEmpty()) {
            log.info("All recommended films were either not found or already rated by user {}", userId);
            return;
        }

        saveRecommendations(userId, resolved);

        log.info("Finished recommendation generation for user {}", userId);
    }

    private List<String> requestAiRecommendations(List<FilmRatingEntity> lastRatings) {
        var prompt = buildPrompt(lastRatings);
        var raw = chatModel.call(prompt);
        return parseTitles(raw);
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
            var rating = r.getRating();
            var title = r.getFilm().getTitle();
            var desc = r.getFilm().getDescription();

            if (desc.length() > 100) desc = desc.substring(0, 100) + "...";

            sb.append(
                "- Title: \"%s\", Rating: %d/5, Context: \"%s\"%n"
                .formatted(title, rating, desc)
            );
        }

        return sb.toString();
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

    private List<Integer> resolveMovies(int userId, List<String> titles) {
        return titles.stream()
                .filter(StringUtils::isNotBlank)
                .flatMap(t -> filmRepository.findIdsByTitleILike(t).stream())
                .filter(id -> !ratingRepository.existsByUserIdAndFilmId(userId, id))
                .toList();
    }

    private void saveRecommendations(Integer userId, List<Integer> filmIds) {
        recommendationRepository.deleteByUserId(userId);

        var entities = filmIds.stream()
                .map(id -> new FilmRecommendationEntity(userId, id))
                .toList();

        recommendationRepository.saveAll(entities);
    }
}