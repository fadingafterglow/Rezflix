package ua.edu.ukma.springers.rezflix.services;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionCriteriaDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionDto;
import ua.edu.ukma.springers.rezflix.criteria.FilmCollectionCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCollectionEntity;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpsertFilmCollectionDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionListDto;
import ua.edu.ukma.springers.rezflix.mappers.FilmCollectionMapper;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;

import java.util.List;

@Service
public class FilmCollectionService extends BaseCRUDService<FilmCollectionEntity, UpsertFilmCollectionDto, UpsertFilmCollectionDto, Integer> {

    private final FilmCollectionMapper mapper;
    private final SecurityUtils securityUtils;

    public FilmCollectionService(FilmCollectionMapper mapper, SecurityUtils securityUtils) {
        super(FilmCollectionEntity.class, FilmCollectionEntity::new);
        this.mapper = mapper;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public FilmCollectionDto getResponseById(int id) {
        return mapper.toResponse(getById(id));
    }

    @Transactional(readOnly = true)
    public FilmCollectionListDto getListResponseByCriteria(FilmCollectionCriteriaDto criteriaDto) {
        FilmCollectionCriteria criteria = new FilmCollectionCriteria(criteriaDto);
        List<FilmCollectionEntity> entities = getList(criteria);
        long total = count(criteria);
        return mapper.toListResponse(total, entities);
    }

    @Override
    protected void postCreate(@NonNull FilmCollectionEntity entity, @NonNull UpsertFilmCollectionDto view) {
        entity.setOwner(securityUtils.getCurrentUser());
    }
}
