package ua.edu.ukma.springers.rezflix.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingEntity;
import ua.edu.ukma.springers.rezflix.domain.embeddables.FilmRatingId;

import org.springframework.data.domain.Pageable;
import java.util.Collection;
import java.util.List;

public interface FilmRatingRepository extends IRepository<FilmRatingEntity, FilmRatingId> {

    List<FilmRatingEntity> findByUserIdAndFilmIdIn(int userId, Collection<Integer> filmIds);

    @EntityGraph(attributePaths = "film")
    @Query("""
        SELECT fr FROM FilmRatingEntity fr
        WHERE fr.user.id = :userId
        ORDER BY fr.createdAt DESC
    """)
    List<FilmRatingEntity> findLastRatingsFetchFilm(int userId, Pageable pageable);

    @Query("""
        SELECT EXISTS (
            SELECT 1 FROM FilmRatingEntity fr
            WHERE fr.user.id = :userId AND fr.film.id = :filmId
        )
    """)
    boolean existsByUserIdAndFilmId(int userId, int filmId);
}
