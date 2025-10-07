package ua.edu.ukma.springers.rezflix.domain.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class FilmRatingId implements Serializable, Comparable<FilmRatingId> {

    @Column(name = "film_id", nullable = false)
    private int filmId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Override
    public int compareTo(FilmRatingId o) {
        int filmCompare = Integer.compare(this.filmId, o.filmId);
        if (filmCompare != 0) return filmCompare;
        return Integer.compare(this.userId, o.userId);
    }
}
