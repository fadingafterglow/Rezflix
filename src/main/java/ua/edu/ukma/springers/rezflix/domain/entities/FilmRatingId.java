package ua.edu.ukma.springers.rezflix.domain.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class FilmRatingId implements Serializable, Comparable<FilmRatingId>{
    @EqualsAndHashCode.Include
    private Integer filmId;
    @EqualsAndHashCode.Include
    private Integer userId;

    @Override
    public int compareTo(FilmRatingId o) {
        int cmp = filmId.compareTo(o.filmId);
        if (cmp != 0) return cmp;
        return userId.compareTo(o.userId);
    }

}
