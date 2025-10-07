package ua.edu.ukma.springers.rezflix.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ua.edu.ukma.springers.rezflix.domain.embeddables.FilmRatingId;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "film_ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FilmRatingEntity implements IGettableById<FilmRatingId> {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private FilmRatingId id;

    @JoinColumn(name = "film_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private FilmEntity film;

    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Min(value = 1, message = "error.film_rating.min")
    @Max(value = 5, message = "error.film_rating.max")
    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
