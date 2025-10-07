package ua.edu.ukma.springers.rezflix.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CommentCriteriaDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCommentEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCommentEntity_;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity_;

import java.util.List;

public class FilmCommentCriteria extends Criteria<FilmCommentEntity, CommentCriteriaDto> {
    public FilmCommentCriteria(CommentCriteriaDto dto) {
        super(FilmCommentEntity.class, dto);
    }

    @Override
    protected <R> List<Predicate> formPredicates(Root<FilmCommentEntity> root, CriteriaQuery<R> query, CriteriaBuilder cb) {
        return new PredicatesBuilder<>(root, cb)
                .eq(values.getFilmId(), FilmCommentEntity_.film, FilmEntity_.id)
                .getPredicates();
    }
}
