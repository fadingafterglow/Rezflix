package ua.edu.ukma.springers.rezflix.repositories;

import org.springframework.data.jpa.repository.Query;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmDubbingEntity;

import java.util.List;
import java.util.Optional;

public interface FilmDubbingRepository extends IRepository<FilmDubbingEntity, Integer> {

    @Query("SELECT fd.id FROM FilmDubbingEntity fd WHERE fd.filmId = :filmId AND fd.name = :name")
    Optional<Integer> findIdByFilmIdAndName(int filmId, String name);

    List<FilmDubbingEntity> findAllByFilmId(int filmId);
}
