package ua.edu.ukma.springers.rezflix.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ua.edu.ukma.criteria.core.Criteria;
import ua.edu.ukma.criteria.core.PredicatesBuilder;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.EpisodeCriteriaDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEpisodeEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEpisodeEntity_;
import ua.edu.ukma.springers.rezflix.domain.enums.FilmEpisodeStatus;
import ua.edu.ukma.springers.rezflix.mappers.EnumsMapper;

import java.util.List;

public class FilmEpisodeCriteria extends Criteria<FilmEpisodeEntity, EpisodeCriteriaDto> {

    private final int dubbingId;
    private final boolean forceRendered;

    private final EnumsMapper enumsMapper;

    public FilmEpisodeCriteria(EpisodeCriteriaDto dto, int dubbingId, boolean forceRendered, EnumsMapper enumsMapper) {
        super(FilmEpisodeEntity.class, dto);
        this.dubbingId = dubbingId;
        this.forceRendered = forceRendered;
        this.enumsMapper = enumsMapper;
    }

    @Override
    protected <R> List<Predicate> formPredicates(Root<FilmEpisodeEntity> root, CriteriaQuery<R> query, CriteriaBuilder cb) {
        var builder = new PredicatesBuilder<>(root, cb)
                .eq(dubbingId, FilmEpisodeEntity_.filmDubbingId)
                .between(values.getMinWatchOrder(), values.getMaxWatchOrder(), FilmEpisodeEntity_.watchOrder)
                .like(values.getQuery(), FilmEpisodeEntity_.title);
        if (forceRendered)
            builder.eq(FilmEpisodeStatus.RENDERED, FilmEpisodeEntity_.status);
        else
            builder.eq(enumsMapper.map(values.getStatus()), FilmEpisodeEntity_.status);
        return builder.getPredicates();
    }
}
