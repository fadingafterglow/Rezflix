package ua.edu.ukma.springers.rezflix.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "film_collections")
public class FilmCollectionEntity implements IGettableById<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Size(max = 250, message = "error.film_collection.name.size")
    @NotBlank(message = "error.film_collection.name.blank")
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 5000, message = "error.film_collection.description.size")
    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(name = "owner_id", insertable = false, updatable = false)
    private int ownerId;

    @Size(max = 50, message = "error.film_collection.size")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "film_collections_films",
            joinColumns = @JoinColumn(name = "collection_id"),
            inverseJoinColumns = @JoinColumn(name = "film_id")
    )
    private Set<FilmEntity> films = new HashSet<>();
}
