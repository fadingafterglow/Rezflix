package ua.edu.ukma.springers.rezflix.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.criteria.FilmCommentCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCommentEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.mappers.FilmCommentMapper;
import ua.edu.ukma.springers.rezflix.repositories.FilmCommentRepository;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FilmCommentServiceTest extends BaseServiceTest<FilmCommentService, FilmCommentEntity, CreateCommentDto, UpdateCommentDto, Integer> {
    @Mock private FilmCommentRepository filmCommentRepository;
    @Mock private FilmCommentMapper mapper;
    @Mock private SecurityUtils securityUtils;
    private static final int COMMENT_ID = 1;

    @Override
    protected FilmCommentService createService() {
        this.repository = filmCommentRepository;
        return new FilmCommentService(mapper, securityUtils);
    }

    @Test
    @DisplayName("Should return comment DTO when entity exists")
    void getResponseById() {
        FilmCommentEntity entity = new FilmCommentEntity();
        entity.setId(COMMENT_ID);
        CommentDto dto = new CommentDto();
        when(repository.findFetchAllById(COMMENT_ID)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(dto);
        CommentDto result = service.getResponseById(COMMENT_ID);
        assertNotNull(result);
        assertEquals(dto, result);
    }

    @Test
    @DisplayName("Should return comment list DTO based on criteria")
    void getListResponseByCriteria() {
        CommentCriteriaDto criteriaDto = new CommentCriteriaDto();
        List<FilmCommentEntity> entities = List.of(new FilmCommentEntity());
        CommentListDto listDto = new CommentListDto();
        when(criteriaRepository.find(any(FilmCommentCriteria.class))).thenReturn(entities);
        when(criteriaRepository.count(any(FilmCommentCriteria.class))).thenReturn(5L);
        when(mapper.toListResponse(5L, entities)).thenReturn(listDto);
        CommentListDto result = service.getListResponseByCriteria(criteriaDto);
        assertEquals(listDto, result);
    }

    @Test
    @DisplayName("Should set current user as author during creation")
    void postCreate() {
        CreateCommentDto dto = new CreateCommentDto();
        UserEntity user = new UserEntity();
        user.setId(100);
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(repository.save(any(FilmCommentEntity.class))).thenAnswer(inv -> {
            FilmCommentEntity e = inv.getArgument(0);
            e.setId(COMMENT_ID);
            return e;
        });
        service.create(dto);
        verify(merger).mergeForCreate(any(FilmCommentEntity.class), eq(dto));
        verify(repository).save(argThat(entity -> entity.getAuthor() != null && entity.getAuthor().getId() == 100));
    }

    @Test
    @DisplayName("Should update comment and evict associated cache entry")
    void update_ShouldEvictCache() {
        UpdateCommentDto dto = new UpdateCommentDto();
        FilmCommentEntity entity = new FilmCommentEntity();
        entity.setId(COMMENT_ID);
        when(repository.findById(COMMENT_ID)).thenReturn(Optional.of(entity));
        when(cacheManager.getCache("filmComment")).thenReturn(cache);
        service.update(COMMENT_ID, dto);
        verify(merger).mergeForUpdate(entity, dto);
        verify(repository).save(entity);
        verify(cache).evict(COMMENT_ID);
    }
}