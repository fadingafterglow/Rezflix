package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmCommentControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.services.FilmCommentService;
import ua.edu.ukma.springers.rezflix.utils.SecurityUtils;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmCommentController implements FilmCommentControllerApi {

    private final FilmCommentService service;
    private final SecurityUtils securityUtils;

    @Override
    public ResponseEntity<CommentDto> getComment(Integer commentId) {
        return ResponseEntity.ok(service.getResponseById(commentId));
    }

    @Override
    public ResponseEntity<CommentListDto> getCommentsByCriteria(CommentCriteriaDto criteria) {
        return ResponseEntity.ok(service.getListResponseByCriteria(criteria));
    }

    @Override
    public ResponseEntity<Integer> createComment(CreateCommentDto dto) {
        log.info("Create comment {} by user {}", dto, securityUtils.getCurrentUserId());
        return ResponseEntity.ok(service.create(dto));
    }

    @Override
    public ResponseEntity<Void> updateComment(Integer commentId, UpdateCommentDto dto) {
        log.info("Update comment {} {} by user {}", commentId, dto, securityUtils.getCurrentUserId());
        service.update(commentId, dto);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteComment(Integer commentId) {
        log.info("Delete comment {} by user {}", commentId, securityUtils.getCurrentUserId());
        service.delete(commentId);
        return ResponseEntity.noContent().build();
    }
}
