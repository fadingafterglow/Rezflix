package ua.edu.ukma.springers.rezflix.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEpisodeEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.FilmEpisodeStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FilmEpisodeRepository extends IRepository<FilmEpisodeEntity, UUID> {

    @Query("SELECT fe.id FROM FilmEpisodeEntity fe WHERE fe.filmDubbingId = :filmDubbingId AND fe.watchOrder = :watchOrder")
    Optional<UUID> findIdByFilmDubbingIdAndWatchOrder(int filmDubbingId, int watchOrder);

    @Modifying
    @Query("UPDATE FilmEpisodeEntity fe SET fe.status = :status WHERE fe.id = :episodeId")
    void updateEpisodeStatus(UUID episodeId, FilmEpisodeStatus status);

    List<FilmEpisodeEntity> findAllByFilmDubbingId(int filmDubbingId);

    boolean existsByIdAndStatus(UUID episodeId, FilmEpisodeStatus status);
}
