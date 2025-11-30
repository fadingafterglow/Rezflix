package ua.edu.ukma.springers.rezflix.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.criteria.FilmDubbingCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmDubbingEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.events.DeleteEntityEvent;
import ua.edu.ukma.springers.rezflix.mappers.FilmDubbingMapper;
import ua.edu.ukma.springers.rezflix.repositories.FilmDubbingRepository;

import java.util.List;

@Service
public class FilmDubbingService extends BaseCRUDService<FilmDubbingEntity, CreateDubbingDto, UpdateDubbingDto, Integer> {

    private static final String CACHE_NAME = "filmDubbing";

    private final FilmDubbingMapper mapper;

    public FilmDubbingService(FilmDubbingMapper mapper) {
        super(FilmDubbingEntity.class, FilmDubbingEntity::new);
        this.mapper = mapper;
    }

    @Cacheable(CACHE_NAME)
    @Transactional(readOnly = true)
    public DubbingDto getResponseById(int id) {
        return mapper.toResponse(getById(id));
    }

    @Transactional(readOnly = true)
    public DubbingListDto getListResponseByCriteria(DubbingCriteriaDto criteriaDto){
        FilmDubbingCriteria criteria = new FilmDubbingCriteria(criteriaDto);
        List<FilmDubbingEntity> entities = getList(criteria);
        long total = count(criteria);
        return mapper.toListResponse(total, entities);
    }

    @Override
    public String getCacheName() {
        return CACHE_NAME;
    }

    @EventListener
    public void clearDubbings(DeleteEntityEvent<? extends FilmEntity, Integer> deleteEvent) {
        ((FilmDubbingRepository) repository).findAllByFilmId(deleteEvent.getId()).forEach(this::deleteEntity);
    }
}
