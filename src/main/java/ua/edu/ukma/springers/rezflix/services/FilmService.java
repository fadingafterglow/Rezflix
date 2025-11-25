package ua.edu.ukma.springers.rezflix.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.aspects.limit.RateLimited;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.criteria.FilmCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.mappers.FilmMapper;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;

import java.util.List;

@Slf4j
@Service
public class FilmService extends BaseCRUDService<FilmEntity, UpsertFilmDto, UpsertFilmDto, Integer> {

    private final FilmMapper mapper;
    private final FilmRepository filmRepository;
    private final FilmRatingService filmRatingService;

    public FilmService(FilmMapper mapper, FilmRepository filmRepository, FilmRatingService filmRatingService) {
        super(FilmEntity.class, FilmEntity::new);
        this.mapper = mapper;
        this.filmRepository = filmRepository;
        this.filmRatingService = filmRatingService;
    }

    @RateLimited
    @Transactional(readOnly = true)
    public FilmDto getResponseById(int id) {
        FilmEntity entity = getById(id);
        return mapper.toResponse(entity, filmRatingService.getCurrentUserRatingForFilms(List.of(id)));
    }

    @Transactional(readOnly = true)
    public FilmListDto getListResponseByCriteria(FilmCriteriaDto criteriaDto) {
        FilmCriteria criteria = new FilmCriteria(criteriaDto);
        List<FilmEntity> entities = getList(criteria);
        long total = count(criteria);
        List<Integer> ids = entities.stream().map(FilmEntity::getId).toList();
        return mapper.toListResponse(total, entities, filmRatingService.getCurrentUserRatingForFilms(ids));
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void recalculateTotalRatings() {
        log.info("Started recalculateTotalRatings");
        filmRepository.recalculateTotalRatings();
        log.info("Finished recalculateTotalRatings");
    }
}
