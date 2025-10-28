package ua.edu.ukma.springers.rezflix.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.criteria.FilmCommentCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCommentEntity;
import ua.edu.ukma.springers.rezflix.mappers.FilmCommentMapper;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;

import java.util.List;

@Service
public class FilmCommentService extends BaseCRUDService<FilmCommentEntity, CreateCommentDto, UpdateCommentDto, Integer> {
    private final FilmCommentMapper mapper;
    private final SecurityUtils securityUtils;

    protected FilmCommentService(FilmCommentMapper mapper, SecurityUtils securityUtils) {
        super(FilmCommentEntity.class, FilmCommentEntity::new);
        this.mapper = mapper;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public CommentDto getResponseById(int id) {
        return mapper.toResponse(getById(id));
    }

    @Transactional(readOnly = true)
    public CommentListDto getListResponseByCriteria(CommentCriteriaDto criteriaDto){
        FilmCommentCriteria criteria = new FilmCommentCriteria(criteriaDto);
        List<FilmCommentEntity> entities = getList(criteria);
        long total = count(criteria);
        return mapper.toListResponse(total, entities);
    }

    @Override
    protected void postCreate(FilmCommentEntity entity, CreateCommentDto dto) {
        entity.setAuthor(securityUtils.getCurrentUser());
    }
}
