package ua.edu.ukma.springers.rezflix.repositories;

import ua.edu.ukma.springers.rezflix.domain.entities.FilmDubbingEntity;

import java.util.Optional;

public interface FilmDubbingRepository extends IRepository<FilmDubbingEntity, Integer> {

    Optional<Integer> findIdByFilmIdAndName(int filmId, String name);
}
