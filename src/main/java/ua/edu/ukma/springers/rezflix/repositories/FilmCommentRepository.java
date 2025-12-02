package ua.edu.ukma.springers.rezflix.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCommentEntity;

import java.util.Optional;

public interface FilmCommentRepository extends IRepository<FilmCommentEntity, Integer> {

    @Override
    @EntityGraph(attributePaths = {"author", "authorRating"})
    Optional<FilmCommentEntity> findFetchAllById(Integer integer);
}
