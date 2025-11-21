package ua.edu.ukma.springers.rezflix.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.aspects.limit.RateLimited;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.criteria.FilmCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.exceptions.NotFoundException;
import ua.edu.ukma.springers.rezflix.mappers.FilmMapper;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;

import java.util.List;

@Slf4j
@Service
public class FilmService extends BaseCRUDService<FilmEntity, UpsertFilmDto, UpsertFilmDto, Integer> {

    private final FilmMapper mapper;
    private final FilmRepository filmRepository;
    private final UserService userService;
    private final FilmRatingService filmRatingService;

    public FilmService(FilmMapper mapper, FilmRepository filmRepository, UserService userService, FilmRatingService filmRatingService) {
        super(FilmEntity.class, FilmEntity::new);
        this.mapper = mapper;
        this.filmRepository = filmRepository;
        this.userService = userService;
        this.filmRatingService = filmRatingService;
    }

    @RateLimited(limitPerMinute = 5)
    @Transactional(readOnly = true)
    public FilmDto getResponseById(int id) {
        FilmDto result = mapper.toResponse(getById(id));

        UserDto curUser = userService.getCurrentUserInfo().getInfo();
        if(curUser == null) {
            return result;
        }

        int userId = curUser.getId();
        try {
            FilmRatingDto rating = filmRatingService.getUserRatingForFilm(userId, id);
            result.setCurrentUserRating(rating);
        } catch (NotFoundException ex) {
            // Ignore, current user hasn't rated this film yet.
        }

        return result;
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
