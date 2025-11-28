package ua.edu.ukma.springers.rezflix.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.springers.rezflix.domain.embeddables.FilmRecommendationId;

@Entity
@Table(name = "film_recommendations")
@IdClass(FilmRecommendationId.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmRecommendationEntity {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Column(name = "film_id")
    private Integer filmId;

}
