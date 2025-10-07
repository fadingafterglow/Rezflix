package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmCommentControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.services.FilmCommentService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmCommentController implements FilmCommentControllerApi {
    private final FilmCommentService service;

    @Override
    public ResponseEntity<Integer> createComment(CreateCommentDto dto) {
        log.info("Create comment for film id {}", dto.getFilmId());
        return ResponseEntity.ok(service.create(dto));
    }

    @Override
    public ResponseEntity<CommentDto> getComment(Integer commentId) {
        return ResponseEntity.ok(service.getResponseById(commentId));
    }

    @Override
    public ResponseEntity<CommentListDto> getCommentsByCriteria(CommentCriteriaDto criteria) {
        return ResponseEntity.ok(service.getListResponseByCriteria(criteria));
    }

    @Override
    public ResponseEntity<Void> updateComment(Integer commentId, UpdateCommentDto dto) {
        log.info("Update comment by id {}", commentId);
        service.update(commentId, dto);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteComment(Integer commentId) {
        log.info("Delete comment by id {}", commentId);
        service.delete(commentId);
        return ResponseEntity.noContent().build();
    }
}
