package ua.edu.ukma.springers.rezflix.repositories;

import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingEntity;
import ua.edu.ukma.springers.rezflix.domain.embeddables.FilmRatingId;

import java.util.Collection;
import java.util.List;

public interface FilmRatingRepository extends IRepository<FilmRatingEntity, FilmRatingId> {

    List<FilmRatingEntity> findByUserIdAndFilmIdIn(int userId, Collection<Integer> filmIds);
}
