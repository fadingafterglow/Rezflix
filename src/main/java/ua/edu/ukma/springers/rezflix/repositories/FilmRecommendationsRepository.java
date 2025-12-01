package ua.edu.ukma.springers.rezflix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ua.edu.ukma.springers.rezflix.domain.embeddables.FilmRecommendationId;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRecommendationEntity;

import java.util.List;

public interface FilmRecommendationsRepository extends JpaRepository<FilmRecommendationEntity, FilmRecommendationId> {

    @Query("""
        SELECT fr.film FROM FilmRecommendationEntity fr
        WHERE fr.userId = :userId
        """)
    List<FilmEntity> findRecommendedFilmsByUserId(int userId);

    @Modifying
    @Query("""
        DELETE FROM FilmRecommendationEntity fr
        WHERE fr.userId = :userId
        """)
    void deleteByUserId(int userId);
}