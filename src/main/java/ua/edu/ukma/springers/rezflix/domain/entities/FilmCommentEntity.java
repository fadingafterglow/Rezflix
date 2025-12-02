package ua.edu.ukma.springers.rezflix.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;

import java.time.LocalDateTime;

@Entity
@Table(name = "film_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FilmCommentEntity implements IGettableById<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @NotBlank(message = "error.film_comment.text.blank")
    @Size(max = 2000, message = "error.film_comment.text.size")
    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", nullable = false)
    private FilmEntity film;

    @Column(name = "film_id", updatable = false, insertable = false)
    private int filmId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @Column(name = "author_id", updatable = false, insertable = false)
    private int authorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", referencedColumnName = "film_id", insertable = false, updatable = false)
    @JoinColumn(name = "author_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private FilmRatingEntity authorRating;
}
