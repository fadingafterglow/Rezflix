package ua.edu.ukma.springers.rezflix.repositories;

import org.springframework.data.jpa.repository.Query;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;

import java.util.Optional;

public interface FilmRepository extends IRepository<FilmEntity, Integer> {

    @Query("SELECT f.id FROM FilmEntity f WHERE f.title = :title")
    Optional<Integer> findIdByTitle(String title);
}
