package ua.edu.ukma.springers.rezflix.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.aspects.limit.RateLimited;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCriteriaDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmDto;
import ua.edu.ukma.springers.rezflix.criteria.FilmCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpsertFilmDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmListDto;
import ua.edu.ukma.springers.rezflix.mappers.FilmMapper;

import java.util.List;

@Service
public class FilmService extends BaseCRUDService<FilmEntity, UpsertFilmDto, UpsertFilmDto, Integer> {

    private static final String CACHE_NAME = "film";

    private final FilmMapper mapper;

    public FilmService(FilmMapper mapper) {
        super(FilmEntity.class, FilmEntity::new);
        this.mapper = mapper;
    }

    @Cacheable(CACHE_NAME)
    @RateLimited(limitPerMinute = 5)
    @Transactional(readOnly = true)
    public FilmDto getResponseById(int id) {
        return mapper.toResponse(getById(id));
    }

    @Transactional(readOnly = true)
    public FilmListDto getListResponseByCriteria(FilmCriteriaDto criteriaDto){
        FilmCriteria criteria = new FilmCriteria(criteriaDto);
        List<FilmEntity> entities = getList(criteria);
        long total = count(criteria);
        return mapper.toListResponse(total, entities);
    }

    @Override
    public String getCacheName() {
        return CACHE_NAME;
    }
}
