package ua.edu.ukma.springers.rezflix.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import ua.edu.ukma.criteria.core.CriteriaRepository;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;
import ua.edu.ukma.springers.rezflix.mergers.IMerger;
import ua.edu.ukma.springers.rezflix.repositories.IRepository;
import ua.edu.ukma.springers.rezflix.validators.IValidator;

@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest<S extends BaseCRUDService<E, CV, UV, I>, E extends IGettableById<I>, CV, UV, I extends Comparable<I>> {
    @Mock protected IRepository<E, I> repository;
    @Mock protected CriteriaRepository criteriaRepository;
    @Mock protected IValidator<E> validator;
    @Mock protected IMerger<E, CV, UV> merger;
    @Mock protected CacheManager cacheManager;
    @Mock protected Cache cache;
    protected S service;

    @BeforeEach
    void baseSetUp() {
        service = createService();
        service.setRepository(repository);
        service.setCriteriaRepository(criteriaRepository);
        service.setValidator(validator);
        service.setMerger(merger);
        service.setCacheManager(cacheManager);
    }

    protected abstract S createService();
}