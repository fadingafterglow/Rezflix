package ua.edu.ukma.springers.rezflix.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;
import java.time.OffsetDateTime;

@Entity
@Table(name = "film_ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilmRatingEntity implements IGettableById<FilmRatingId> {

    @EmbeddedId
    private FilmRatingId id;

    @MapsId("filmId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", nullable = false)
    private FilmEntity film;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @NotNull(message = "error.film_rating.null")
    @Min(value = 1, message = "error.film_rating.min")
    @Max(value = 5, message = "error.film_rating.max")
    @Column(name = "rating")
    private Integer rating;

    @NotNull(message = "error.film_rating.created_at.null")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
