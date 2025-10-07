package ua.edu.ukma.springers.rezflix.services;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.criteria.Criteria;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;
import ua.edu.ukma.springers.rezflix.exceptions.NotFoundException;
import ua.edu.ukma.springers.rezflix.mergers.IMerger;
import ua.edu.ukma.springers.rezflix.repositories.CriteriaRepository;
import ua.edu.ukma.springers.rezflix.repositories.IRepository;
import ua.edu.ukma.springers.rezflix.validators.IValidator;

import java.util.List;
import java.util.function.Supplier;

public abstract class BaseCRUDService<E extends IGettableById<I>, CV, UV, I extends Comparable<I>> implements ICRUDService<E, CV, UV, I> {

    @Setter(onMethod_ = @Autowired)
    protected IRepository<E, I> repository;
    @Setter(onMethod_ = @Autowired)
    protected CriteriaRepository criteriaRepository;
    @Setter(onMethod_ = @Autowired)
    protected IValidator<E> validator;
    @Setter(onMethod_ = @Autowired)
    protected IMerger<E, CV, UV> merger;

    protected final Class<E> entityClass;
    protected final Supplier<E> entitySupplier;

    protected BaseCRUDService(Class<E> entityClass, Supplier<E> entitySupplier) {
        this.entityClass = entityClass;
        this.entitySupplier = entitySupplier;
    }

    @Transactional(readOnly = true)
    public E getByIdWithoutValidation(@NonNull I id) {
        return repository.findById(id).orElseThrow(() -> notFound(id));
    }

    @Transactional(readOnly = true)
    public E getByIdFetchAllWithoutValidation(@NonNull I id) {
        return repository.findByIdFetchAll(id).orElseThrow(() -> notFound(id));
    }

    @Override
    @Transactional(readOnly = true)
    public E getById(@NonNull I id) {
        E entity = getByIdFetchAllWithoutValidation(id);
        validator.validForView(entity);
        return entity;
    }

    @Override
    @Transactional(readOnly = true)
    public List<E> getList(@NonNull Criteria<E, ?> criteria) {
        List<E> entities = criteriaRepository.find(criteria);
        validator.validForView(entities);
        return entities;
    }

    @Override
    @Transactional(readOnly = true)
    public long count(@NonNull Criteria<E, ?> criteria) {
        return criteriaRepository.count(criteria);
    }

    @Override
    @Transactional
    public I create(@NonNull CV view) {
        return createEntity(view).getId();
    }

    @Transactional
    public E createEntity(@NonNull CV view) {
        E entity = entitySupplier.get();
        merger.mergeForCreate(entity, view);
        postCreate(entity, view);
        validator.validForCreate(entity);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public boolean update(@NonNull I id, @NonNull UV view) {
        E entity = getByIdWithoutValidation(id);
        merger.mergeForUpdate(entity, view);
        postUpdate(entity, view);
        validator.validForUpdate(entity);
        repository.save(entity);
        return true;
    }

    @Override
    @Transactional
    public void delete(@NonNull I id) {
        E entity = getByIdWithoutValidation(id);
        validator.validForDelete(entity);
        repository.delete(entity);
    }

    protected NotFoundException notFound(I id) {
        return new NotFoundException(entityClass, "id: " + id);
    }

    protected void postCreate(@NonNull E entity, @NonNull CV view) {
    }

    protected void postUpdate(@NonNull E entity, @NonNull UV view) {
    }
}