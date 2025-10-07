package ua.edu.ukma.springers.rezflix.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionCriteriaDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCollectionEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCollectionEntity_;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity_;

import java.util.List;

public class FilmCollectionCriteria extends Criteria<FilmCollectionEntity, FilmCollectionCriteriaDto> {

    public FilmCollectionCriteria(FilmCollectionCriteriaDto dto) {
        super(FilmCollectionEntity.class, dto);
    }

    @Override
    protected <R> List<Predicate> formPredicates(Root<FilmCollectionEntity> root,
                                                 CriteriaQuery<R> query,
                                                 CriteriaBuilder cb) {
        PredicatesBuilder<FilmCollectionEntity> builder = new PredicatesBuilder<>(root, cb);
        builder.like(values.getQuery(), FilmCollectionEntity_.name, FilmCollectionEntity_.description);
        builder.eq(values.getOwnerId(), FilmCollectionEntity_.owner, UserEntity_.id);

        return builder.getPredicates();
    }
}
