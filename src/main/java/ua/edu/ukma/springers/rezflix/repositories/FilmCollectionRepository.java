package ua.edu.ukma.springers.rezflix.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCollectionEntity;

import java.util.Optional;

public interface FilmCollectionRepository extends IRepository<FilmCollectionEntity, Integer> {

    @Override
    @EntityGraph(attributePaths = {"owner", "films"})
    Optional<FilmCollectionEntity> findFetchAllById(Integer integer);
}
