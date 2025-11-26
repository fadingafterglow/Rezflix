package ua.edu.ukma.springers.rezflix.criteria;

import jakarta.persistence.criteria.*;
import ua.edu.ukma.criteria.core.Criteria;
import ua.edu.ukma.criteria.core.PredicatesBuilder;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CommentCriteriaDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCommentEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCommentEntity_;

import java.util.List;

public class FilmCommentCriteria extends Criteria<FilmCommentEntity, CommentCriteriaDto> {

    public FilmCommentCriteria(CommentCriteriaDto dto) {
        super(FilmCommentEntity.class, dto);
    }

    @Override
    protected <R> List<Predicate> formPredicates(Root<FilmCommentEntity> root, CriteriaQuery<R> query, CriteriaBuilder cb) {
        return new PredicatesBuilder<>(root, cb)
                .eq(values.getFilmId(), FilmCommentEntity_.filmId)
                .getPredicates();
    }

    @Override
    protected void fetch(CriteriaBuilder cb, Root<FilmCommentEntity> root) {
        root.fetch(FilmCommentEntity_.author);
        root.fetch(FilmCommentEntity_.authorRating, JoinType.LEFT);
    }
}
