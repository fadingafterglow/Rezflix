package ua.edu.ukma.springers.rezflix.repositories;

import org.springframework.data.jpa.repository.Query;
import ua.edu.ukma.springers.rezflix.domain.entities.FileInfoEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.FileType;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface FileInfoRepository extends IRepository<FileInfoEntity, UUID> {

    List<FileInfoEntity> findAllByFileTypeAndBoundEntityIdIn(FileType fileType, Collection<Integer> boundEntitiesIds);

    @Query(value =
        """
        DELETE FROM files_info
        WHERE file_type = cast(:#{#fileType.name()} AS file_type) AND bound_entity_id = :boundEntityId
        RETURNING id
        """, nativeQuery = true
    )
    // do not use @Modifying because of the RETURNING clause
    List<UUID> deleteByFileTypeAndBoundEntityId(FileType fileType, int boundEntityId);
}
