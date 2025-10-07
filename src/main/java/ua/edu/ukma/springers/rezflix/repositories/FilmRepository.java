package ua.edu.ukma.springers.rezflix.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;

import java.util.Optional;

public interface FilmRepository extends IRepository<FilmEntity, Integer> {

    @Query("SELECT f.id FROM FilmEntity f WHERE f.title = :title")
    Optional<Integer> findIdByTitle(@Param("title") String title);
}
