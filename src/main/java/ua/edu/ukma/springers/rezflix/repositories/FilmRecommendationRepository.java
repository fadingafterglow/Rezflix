package ua.edu.ukma.springers.rezflix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.ukma.springers.rezflix.domain.embeddables.FilmRecommendationId;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRecommendationEntity;

import java.util.List;

@Repository
public interface FilmRecommendationRepository extends JpaRepository<FilmRecommendationEntity, FilmRecommendationId> {

    List<FilmRecommendationEntity> findAllByUserId(Integer userId);

    void deleteByUserId(Integer userId);
}