package ua.edu.ukma.springers.rezflix.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingEntity;
import ua.edu.ukma.springers.rezflix.domain.embeddables.FilmRatingId;

import org.springframework.data.domain.Pageable;
import java.util.Collection;
import java.util.List;

public interface FilmRatingRepository extends IRepository<FilmRatingEntity, FilmRatingId> {

    List<FilmRatingEntity> findByUserIdAndFilmIdIn(int userId, Collection<Integer> filmIds);

    @Query("""
        SELECT fr FROM FilmRatingEntity fr
        WHERE fr.user.id = :userId
        ORDER BY fr.createdAt DESC
    """)
    List<FilmRatingEntity> findLastRatings(@Param("userId") Integer userId, Pageable pageable);

    @Query("""
        SELECT COUNT(fr) > 0 FROM FilmRatingEntity fr
        WHERE fr.user.id = :userId AND fr.film.id = :filmId
    """)
    boolean existsByUserIdAndFilmId(@Param("userId") Integer userId,
                                    @Param("filmId") Integer filmId);
}
