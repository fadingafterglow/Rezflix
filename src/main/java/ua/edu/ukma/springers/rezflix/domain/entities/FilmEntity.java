package ua.edu.ukma.springers.rezflix.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "films")
public class FilmEntity implements IGettableById<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Size(max = 250, message = "error.film.title.size")
    @NotBlank(message = "error.film.title.blank")
    @Column(name = "title", unique = true, nullable = false)
    private String title;

    @Size(max = 5000, message = "error.film.description.size")
    @NotBlank(message = "error.film.description.blank")
    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(
            mappedBy = "film",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<FilmCommentEntity> comments;
}