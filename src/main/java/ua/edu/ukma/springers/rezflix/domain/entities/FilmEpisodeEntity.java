package ua.edu.ukma.springers.rezflix.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import ua.edu.ukma.springers.rezflix.domain.enums.FilmEpisodeStatus;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;

import java.util.UUID;

@Entity
@Table(name = "film_episodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FilmEpisodeEntity implements IGettableById<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "film_dubbing_id", nullable = false)
    private FilmDubbingEntity filmDubbing;

    @Column(name = "film_dubbing_id", updatable = false, insertable = false)
    private int filmDubbingId;

    @Min(value = 0, message = "error.film_episode.watch_order.min")
    @Column(name = "watch_order", nullable = false)
    private int watchOrder;

    @Size(max = 250, message = "error.film_episode.title.size")
    @Column(name = "title")
    private String title;

    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false)
    private FilmEpisodeStatus status;
}
