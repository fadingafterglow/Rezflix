package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmContentControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmContentUrlResponseDto;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmContentController implements FilmContentControllerApi {
    @Override
    public ResponseEntity<Void> deleteFilmContent(Integer filmId) {
        log.info("Delete film content for film id {}", filmId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<FilmContentUrlResponseDto> getFilmContentUrl(Integer filmId) {
        return ResponseEntity.ok(new FilmContentUrlResponseDto("https://www.youtube.com/watch?v=oHg5SJYRHA0"));
    }

    @Override
    public ResponseEntity<Void> uploadFilmContent(Integer filmId, MultipartFile file) {
        log.info("Upload film content for film id {}", filmId);
        return ResponseEntity.noContent().build();
    }
}
