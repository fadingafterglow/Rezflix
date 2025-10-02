package ua.edu.ukma.springers.rezflix.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ua.edu.ukma.springers.rezflix.criteria.FilmCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpsertFilmDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmListDto;
import ua.edu.ukma.springers.rezflix.exceptions.NotFoundException;
import ua.edu.ukma.springers.rezflix.mappers.FilmMapper;

import java.util.List;

@Service
public class FilmService extends BaseCRUDService<FilmEntity, UpsertFilmDto, UpsertFilmDto, Integer> {

    private FilmMapper mapper;

    protected FilmService() {
        super(FilmEntity.class, FilmEntity::new);
    }

    @Transactional
    public FilmListDto getFilmListByCruteria(ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCriteriaDto criteriaDto){
        FilmCriteria criteria = new FilmCriteria(criteriaDto);
        List<FilmEntity> entities = getList(criteria);
        return mapper.toListResponse(count(criteria), entities);
    }
}
