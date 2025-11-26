package ua.edu.ukma.springers.rezflix.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ua.edu.ukma.criteria.core.Criteria;
import ua.edu.ukma.criteria.core.PredicatesBuilder;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.DubbingCriteriaDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmDubbingEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmDubbingEntity_;

import java.util.List;

public class FilmDubbingCriteria extends Criteria<FilmDubbingEntity, DubbingCriteriaDto> {

    public FilmDubbingCriteria(DubbingCriteriaDto dto) {
        super(FilmDubbingEntity.class, dto);
    }

    @Override
    protected <R> List<Predicate> formPredicates(Root<FilmDubbingEntity> root, CriteriaQuery<R> query, CriteriaBuilder cb) {
        return new PredicatesBuilder<>(root, cb)
                .eq(values.getFilmId(), FilmDubbingEntity_.filmId)
                .like(values.getQuery(), FilmDubbingEntity_.name)
                .getPredicates();
    }
}
