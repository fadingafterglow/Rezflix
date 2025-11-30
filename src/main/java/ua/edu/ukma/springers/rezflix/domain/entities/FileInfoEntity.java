package ua.edu.ukma.springers.rezflix.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import ua.edu.ukma.springers.rezflix.domain.enums.FileType;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "files_info")
public class FileInfoEntity implements IGettableById<UUID> {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    @Column(name = "bound_entity_id", nullable = false)
    private int boundEntityId;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;
}