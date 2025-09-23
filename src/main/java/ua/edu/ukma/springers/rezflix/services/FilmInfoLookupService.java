package ua.edu.ukma.springers.rezflix.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmInfoLookupResultDto;
import ua.edu.ukma.springers.rezflix.exceptions.NotFoundException;
import ua.edu.ukma.springers.rezflix.mappers.FilmInfoLookupMapper;

import java.util.List;
import java.util.Objects;

@Service
public class FilmInfoLookupService {

    private final FilmInfoLookupMapper mapper;
    private final RestClient restClient;

    private final String apiKey;

    public FilmInfoLookupService(FilmInfoLookupMapper mapper, @Value("${api-keys.omdb}") String apiKey) {
        this.mapper = mapper;
        this.apiKey = apiKey;

        ObjectMapper jsonMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .build();
        this.restClient = RestClient.builder()
            .messageConverters(l -> {
                l.removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
                l.add(new MappingJackson2HttpMessageConverter(jsonMapper));
            })
            .build();
    }

    public FilmInfoLookupResultDto lookupFilmInfo(String title) {
        FilmInfoLookupApiResponse apiResponse = restClient.get()
            .uri("http://www.omdbapi.com/?apikey={apiKey}&t={title}", apiKey, title)
            .retrieve()
            .body(FilmInfoLookupApiResponse.class);
        if (apiResponse == null || !Objects.equals("True", apiResponse.response()))
            throw new NotFoundException();
        return mapper.map(apiResponse);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FilmInfoLookupApiResponse(
            String title,
            String year,
            String rated,
            String released,
            String runtime,
            String genre,
            String director,
            String writer,
            String actors,
            String plot,
            String language,
            String country,
            String awards,
            String poster,
            List<Rating> ratings,
            String metascore,
            String imdbRating,
            String imdbVotes,
            String imdbID,
            String type,
            String response
    ) {
        public record Rating(
                String source,
                String value
        ) {}
    }
}
