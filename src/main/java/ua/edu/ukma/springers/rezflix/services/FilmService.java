package ua.edu.ukma.springers.rezflix.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;

import java.util.List;

@Slf4j
@Service
public class FilmService extends BaseCRUDService<FilmEntity, UpsertFilmDto, UpsertFilmDto, Integer> {

    private final FilmMapper mapper;
    private final FilmRepository filmRepository;

    public FilmService(FilmMapper mapper, FilmRepository filmRepository) {
        super(FilmEntity.class, FilmEntity::new);
        this.mapper = mapper;
        this.filmRepository = filmRepository;
    }

    @RateLimited(limitPerMinute = 5)
    @Transactional(readOnly = true)
    public FilmDto getResponseById(int id) {
        return mapper.toResponse(getById(id));
    }

    @Transactional(readOnly = true)
    public FilmListDto getListResponseByCriteria(FilmCriteriaDto criteriaDto) {
        FilmCriteria criteria = new FilmCriteria(criteriaDto);
        List<FilmEntity> entities = getList(criteria);
        long total = count(criteria);
        return mapper.toListResponse(total, entities);
    }

    @Scheduled(cron = "59 59 23 * * *")
    @Transactional
    public void recalculateTotalRatings() {
        log.info("Started recalculateTotalRatings");
        filmRepository.recalculateTotalRatings();
        log.info("Ended recalculateTotalRatings");
    }
}
