package ua.edu.ukma.springers.rezflix.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionCriteriaDto;
import ua.edu.ukma.springers.rezflix.criteria.FilmCollectionCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCollectionEntity;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpsertFilmCollectionDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionListDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.mappers.FilmCollectionMapper;
import ua.edu.ukma.springers.rezflix.utils.SecurityUtils;

import java.util.List;

@Service
public class FilmCollectionService extends BaseCRUDService<FilmCollectionEntity, UpsertFilmCollectionDto, UpsertFilmCollectionDto, Integer> {

    private final FilmCollectionMapper mapper;
    private final SecurityUtils securityUtils;
    private final FilmService filmService;

    public FilmCollectionService(FilmCollectionMapper mapper, SecurityUtils securityUtils, FilmService filmService) {
        super(FilmCollectionEntity.class, FilmCollectionEntity::new);
        this.mapper = mapper;
        this.securityUtils = securityUtils;
        this.filmService = filmService;
    }

    @Override
    @Transactional
    public FilmCollectionEntity createEntity(UpsertFilmCollectionDto dto) {
        FilmCollectionEntity entity = super.createEntity(dto);
        entity.setOwner(securityUtils.getCurrentUser());
        return repository.save(entity);
    }

    @Transactional(readOnly = true)
    public FilmCollectionListDto getListResponseByCriteria(FilmCollectionCriteriaDto criteriaDto) {
        FilmCollectionCriteria criteria = new FilmCollectionCriteria(criteriaDto);
        List<FilmCollectionEntity> entities = getList(criteria);
        long total = count(criteria);
        return mapper.toListResponse(total, entities);
    }

    @Transactional
    public void addFilmToCollection(Integer collectionId, Integer filmId) {
        FilmCollectionEntity collection = getByIdWithoutValidation(collectionId);
        FilmEntity film = filmService.getByIdWithoutValidation(filmId);

        collection.getFilms().add(film);
        repository.save(collection);
    }

    @Transactional
    public void removeFilmFromCollection(Integer collectionId, Integer filmId) {
        FilmCollectionEntity collection = getByIdWithoutValidation(collectionId);
        FilmEntity film = filmService.getByIdWithoutValidation(filmId);

        collection.getFilms().remove(film);
        repository.save(collection);
    }

    @Transactional
    public void clearCollection(Integer collectionId) {
        FilmCollectionEntity collection = getByIdWithoutValidation(collectionId);
        collection.getFilms().clear();
        repository.save(collection);
    }
}
