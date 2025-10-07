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
        return new PredicatesBuilder<>(root, cb)
                .like(values.getQuery(), FilmEntity_.title, FilmEntity_.description)
                .getPredicates();
    }

}
