package ua.edu.ukma.springers.rezflix.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.criteria.FilmCommentCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCommentEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.events.DeleteEntityEvent;
import ua.edu.ukma.springers.rezflix.mappers.FilmCommentMapper;
import ua.edu.ukma.springers.rezflix.repositories.FilmCommentRepository;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;

import java.util.List;

@Service
public class FilmCommentService extends BaseCRUDService<FilmCommentEntity, CreateCommentDto, UpdateCommentDto, Integer> {

    private static final String CACHE_NAME = "filmComment";

    private final FilmCommentMapper mapper;
    private final SecurityUtils securityUtils;

    public FilmCommentService(FilmCommentMapper mapper, SecurityUtils securityUtils) {
        super(FilmCommentEntity.class, FilmCommentEntity::new);
        this.mapper = mapper;
        this.securityUtils = securityUtils;
    }

    @Cacheable(CACHE_NAME)
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
    protected void postCreate(@NonNull FilmCommentEntity entity, @NonNull CreateCommentDto dto) {
        entity.setAuthor(securityUtils.getCurrentUser());
    }

    @Override
    public String getCacheName() {
        return CACHE_NAME;
    }

    @EventListener
    public void clearComments(DeleteEntityEvent<? extends UserEntity, Integer> event) {
        ((FilmCommentRepository) repository).deleteAllByAuthorId(event.getId());
    }
}
