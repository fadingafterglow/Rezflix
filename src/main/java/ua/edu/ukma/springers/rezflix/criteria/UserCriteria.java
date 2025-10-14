package ua.edu.ukma.springers.rezflix.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ua.edu.ukma.criteria.core.Criteria;
import ua.edu.ukma.criteria.core.PredicatesBuilder;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UserCriteriaDto;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity_;
import ua.edu.ukma.springers.rezflix.mappers.EnumsMapper;

import java.util.List;

public class UserCriteria extends Criteria<UserEntity, UserCriteriaDto> {

    private final EnumsMapper enumsMapper;

    public UserCriteria(UserCriteriaDto criteriaDto, EnumsMapper enumsMapper) {
        super(UserEntity.class, criteriaDto);
        this.enumsMapper = enumsMapper;
    }

    @Override
    protected <R> List<Predicate> formPredicates(Root<UserEntity> root, CriteriaQuery<R> query, CriteriaBuilder cb) {
        return new PredicatesBuilder<>(root, cb)
                .like(values.getQuery(), UserEntity_.username)
                .eq(enumsMapper.map(values.getType()), UserEntity_.type)
                .getPredicates();
    }
}
