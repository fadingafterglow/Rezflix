package ua.edu.ukma.springers.rezflix.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import ua.edu.ukma.springers.rezflix.domain.embeddables.FilmRecommendationId;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "film_recommendations")
@IdClass(FilmRecommendationId.class)
public class FilmRecommendationEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    @EqualsAndHashCode.Include
    private int userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @Id
    @Column(name = "film_id", nullable = false)
    @EqualsAndHashCode.Include
    private int filmId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", insertable = false, updatable = false)
    private FilmEntity film;

    public FilmRecommendationEntity(int userId, int filmId) {
        this.userId = userId;
        this.filmId = filmId;
    }
}
