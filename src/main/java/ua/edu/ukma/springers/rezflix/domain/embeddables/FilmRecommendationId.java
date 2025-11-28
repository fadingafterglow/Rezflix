package ua.edu.ukma.springers.rezflix.domain.embeddables;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmRecommendationId implements Serializable {
    private Integer userId;
    private Integer filmId;
}