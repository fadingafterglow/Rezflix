package ua.edu.ukma.springers.rezflix.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ua.edu.ukma.springers.rezflix.criteria.Criteria;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CriteriaRepository {

    private final EntityManager entityManager;

    public <T> List<T> find(Criteria<T, ?> criteria) {
        TypedQuery<T> query = criteria.createQuery(entityManager);
        if (criteria.getSize() != null && criteria.getSize() > 0) {
            query.setMaxResults(criteria.getSize());
            if (criteria.getPage() != null && criteria.getPage() > 0) {
                query.setFirstResult(criteria.getPage() * criteria.getSize());
            }
        }

        return query.getResultList();
    }

    public long count(Criteria<?, ?> criteria) {
        TypedQuery<Long> query = criteria.createCountQuery(entityManager);
        return query.getSingleResult();
    }
}
