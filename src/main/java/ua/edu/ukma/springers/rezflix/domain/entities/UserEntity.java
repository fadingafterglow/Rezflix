package ua.edu.ukma.springers.rezflix.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class UserEntity implements IGettableById<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "error.user.type.null")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private UserType type;

    @EqualsAndHashCode.Include
    @NotBlank(message = "error.user.username.blank")
    @Size(max = 64, message = "error.user.username.size")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotNull(message = "error.user.password.null")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Size(max = 5000, message = "error.user.about.size")
    @Column(name = "about")
    private String about;
}
