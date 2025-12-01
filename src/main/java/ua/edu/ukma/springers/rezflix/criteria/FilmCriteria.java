package ua.edu.ukma.springers.rezflix.criteria;

import jakarta.persistence.criteria.*;
import ua.edu.ukma.criteria.core.Criteria;
import ua.edu.ukma.criteria.core.PredicatesBuilder;
import ua.edu.ukma.springers.rezflix.domain.entities.*;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCriteriaDto;

import java.util.List;

public class FilmCriteria extends Criteria<FilmEntity, FilmCriteriaDto> {

    public FilmCriteria(FilmCriteriaDto filmCriteriaDto) {
        super(FilmEntity.class, filmCriteriaDto);
    }

    @Override
    protected <R> List<Predicate> formPredicates(Root<FilmEntity> root, CriteriaQuery<R> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new PredicatesBuilder<>(root, cb)
                .like(values.getQuery(), FilmEntity_.title, FilmEntity_.description)
                .getPredicates();

        if (values.getRatedByUser() != null) {
            Predicate predicate = cb.exists(ratingSubquery(values.getRatedByUser(), root, query, cb));
            predicates.add(predicate);
        }
        if (values.getNotRatedByUser() != null) {
            Predicate predicate = cb.not(cb.exists(ratingSubquery(values.getNotRatedByUser(), root, query, cb)));
            predicates.add(predicate);
        }

        return predicates;
    }

    private <R> Subquery<FilmRatingEntity> ratingSubquery(int userId, Root<FilmEntity> root, CriteriaQuery<R> query, CriteriaBuilder cb) {
        Subquery<FilmRatingEntity> subquery = query.subquery(FilmRatingEntity.class);
        Root<FilmRatingEntity> subRoot = subquery.from(FilmRatingEntity.class);
        subquery.select(subRoot);
        subquery.where(
            cb.and(
                cb.equal(subRoot.get(FilmRatingEntity_.film).get(FilmEntity_.id), root.get(FilmEntity_.id)),
                cb.equal(subRoot.get(FilmRatingEntity_.user).get(UserEntity_.id), userId)
            )
        );
        return subquery;
    }

}
