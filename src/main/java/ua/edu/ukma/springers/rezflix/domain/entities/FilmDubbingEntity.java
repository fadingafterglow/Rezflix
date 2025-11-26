package ua.edu.ukma.springers.rezflix.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;

@Entity
@Table(name = "film_dubbings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FilmDubbingEntity implements IGettableById<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", nullable = false)
    private FilmEntity film;

    @Column(name = "film_id", updatable = false, insertable = false)
    private int filmId;

    @NotBlank(message = "error.film_dubbing.name.blank")
    @Size(max = 250, message = "error.film_dubbing.name.size")
    @Column(name = "name", nullable = false)
    private String name;
}
