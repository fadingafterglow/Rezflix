package ua.edu.ukma.springers.rezflix.services;

import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import ua.edu.ukma.criteria.core.Criteria;
import ua.edu.ukma.criteria.core.CriteriaRepository;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;
import ua.edu.ukma.springers.rezflix.events.DeleteEntityEvent;
import ua.edu.ukma.springers.rezflix.exceptions.NotFoundException;
import ua.edu.ukma.springers.rezflix.mergers.IMerger;
import ua.edu.ukma.springers.rezflix.repositories.IRepository;
import ua.edu.ukma.springers.rezflix.validators.IValidator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseCRUDServiceTest {

    @Mock IRepository<TestEntity, Integer> repository;
    @Mock CriteriaRepository criteriaRepository;
    @Mock IValidator<TestEntity> validator;
    @Mock IMerger<TestEntity, TestDto, TestDto> merger;
    @Mock CacheManager cacheManager;
    @Mock Cache cache;
    @Mock ApplicationEventPublisher eventPublisher;

    private static final int EXISTING_ID = 1;
    private static final int MISSING_ID = 999;
    private TestEntity existingEntity;

    private ConcreteCRUDService service;

    @BeforeEach
    void setUp() {
        service = new ConcreteCRUDService();
        service.setRepository(repository);
        service.setCriteriaRepository(criteriaRepository);
        service.setValidator(validator);
        service.setMerger(merger);
        service.setCacheManager(cacheManager);
        service.setEventPublisher(eventPublisher);

        existingEntity = new TestEntity(EXISTING_ID, "test");
        lenient().when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));
        lenient().when(repository.findFetchAllById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));
        lenient().when(repository.findById(MISSING_ID)).thenReturn(Optional.empty());
        lenient().when(repository.findFetchAllById(MISSING_ID)).thenReturn(Optional.empty());
        lenient().when(cacheManager.getCache("testCache")).thenReturn(cache);
    }

    @Test
    void getByIdWithoutValidation() {
        TestEntity result = service.getByIdWithoutValidation(EXISTING_ID);
        assertEquals(existingEntity, result);
        verify(repository).findById(EXISTING_ID);
    }

    @Test
    void getByIdWithoutValidation_NotFound() {
        assertThrows(NotFoundException.class, () -> service.getByIdWithoutValidation(MISSING_ID));
        verify(repository).findById(MISSING_ID);
    }

    @Test
    void getByIdFetchAllWithoutValidation() {
        TestEntity result = service.getByIdFetchAllWithoutValidation(EXISTING_ID);
        assertEquals(existingEntity, result);
        verify(repository).findFetchAllById(EXISTING_ID);
    }

    @Test
    void getByIdFetchAllWithoutValidation_NotFound() {
        assertThrows(NotFoundException.class, () -> service.getByIdFetchAllWithoutValidation(MISSING_ID));
        verify(repository).findFetchAllById(MISSING_ID);
    }

    @Test
    void getById() {
        TestEntity result = service.getById(EXISTING_ID);
        assertEquals(existingEntity, result);
        verify(validator).validForView(existingEntity);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getList() {
        Criteria<TestEntity, ?> criteria = mock(Criteria.class);
        List<TestEntity> list = Collections.singletonList(existingEntity);
        doReturn(list).when(criteriaRepository).find(any());
        List<TestEntity> result = service.getList(criteria);
        assertEquals(list, result);
        verify(validator).validForView(list);
    }

    @Test
    @SuppressWarnings("unchecked")
    void count() {
        Criteria<TestEntity, ?> criteria = mock(Criteria.class);
        when(criteriaRepository.count(any())).thenReturn(10L);
        assertEquals(10L, service.count(criteria));
    }

    @Test
    void create() {
        TestDto dto = new TestDto("val");
        when(repository.save(any(TestEntity.class))).thenAnswer(invocation -> {
            TestEntity e = invocation.getArgument(0);
            e.setId(EXISTING_ID);
            return e;
        });
        Integer id = service.create(dto);
        assertEquals(EXISTING_ID, id);
        verify(merger).mergeForCreate(any(TestEntity.class), eq(dto));
        verify(validator).validForCreate(any(TestEntity.class));
        verify(repository).save(any(TestEntity.class));
    }

    @Test
    void createWithId() {
        TestDto dto = new TestDto("val");
        when(repository.save(any(TestEntity.class))).thenAnswer(i -> i.getArgument(0));
        Integer id = service.create(EXISTING_ID, dto);
        assertEquals(EXISTING_ID, id);
    }

    @Test
    void update() {
        TestDto dto = new TestDto("update");
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));
        boolean updated = service.update(EXISTING_ID, dto);
        assertTrue(updated);
        verify(merger).mergeForUpdate(existingEntity, dto);
        verify(validator).validForUpdate(existingEntity);
        verify(repository).save(existingEntity);
        verify(cache).evict(EXISTING_ID);
    }

    @Test
    void delete() {
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));
        service.delete(EXISTING_ID);
        verify(validator).validForDelete(existingEntity);
        verify(eventPublisher).publishEvent(any(DeleteEntityEvent.class));
        verify(repository).delete(existingEntity);
        verify(cache).evict(EXISTING_ID);
    }

    @Test
    void evictIfCached_WhenCacheNameIsNull() {
        ConcreteCRUDServiceNoCache noCacheService = new ConcreteCRUDServiceNoCache();
        noCacheService.setCacheManager(cacheManager);
        noCacheService.evictIfCached(123);
        verify(cacheManager, never()).getCache(any());
    }

    @Data
    static class TestEntity implements IGettableById<Integer> {
        private Integer id;
        private String data;
        public TestEntity() {}
        public TestEntity(Integer id, String data) { this.id = id; this.data = data; }
        @Override public Integer getId() { return id; }
        @Override public void setId(Integer id) { this.id = id; }
    }

    static class TestDto {
        String data;
        public TestDto(String data) { this.data = data; }
    }
    static class ConcreteCRUDService extends BaseCRUDService<TestEntity, TestDto, TestDto, Integer> {
        public ConcreteCRUDService() { super(TestEntity.class, TestEntity::new); }
        @Override protected String getCacheName() { return "testCache"; }
    }
    static class ConcreteCRUDServiceNoCache extends BaseCRUDService<TestEntity, TestDto, TestDto, Integer> {
        public ConcreteCRUDServiceNoCache() { super(TestEntity.class, TestEntity::new); }
    }
}