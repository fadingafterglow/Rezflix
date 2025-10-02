package ua.edu.ukma.springers.rezflix.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCriteriaDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity_;


import java.util.List;

public class FilmCriteria extends Criteria<FilmEntity, FilmCriteriaDto> {
    public FilmCriteria(FilmCriteriaDto filmCriteriaDto) {
        super(FilmEntity.class, filmCriteriaDto);
    }

    @Override
    protected <R> List<Predicate> formPredicates(Root<FilmEntity> root, CriteriaQuery<R> query, CriteriaBuilder cb) {
        PredicatesBuilder<FilmEntity> builder = new PredicatesBuilder<>(root, cb);

        if (values.getOrderBy() != null && !values.getOrderBy().isEmpty()) {
            builder.like(values.getOrderBy(), FilmEntity_.title);
        }

        return builder.getPredicates();
    }

}
