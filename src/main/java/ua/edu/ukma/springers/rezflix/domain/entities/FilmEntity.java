package ua.edu.ukma.springers.rezflix.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;

@Entity
@Table(name = "films")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilmEntity implements IGettableById<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "error.film.title.null")
    @NotBlank(message = "error.film.title.blank")
    @Column(name = "title")
    private String title;

    @NotBlank(message = "error.film.description.blank")
    @Column(name = "name")
    private String description;

}