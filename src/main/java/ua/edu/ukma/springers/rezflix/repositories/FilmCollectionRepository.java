package ua.edu.ukma.springers.rezflix.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCollectionEntity;

import java.util.Optional;

public interface FilmCollectionRepository extends IRepository<FilmCollectionEntity, Integer> {

    @Override
    @EntityGraph(attributePaths = {"owner", "films"})
    Optional<FilmCollectionEntity> findFetchAllById(Integer id);

    @Modifying
    @Query("DELETE FROM FilmCollectionEntity fc WHERE fc.ownerId = :ownerId")
    void deleteAllByOwnerId(int ownerId);
}
