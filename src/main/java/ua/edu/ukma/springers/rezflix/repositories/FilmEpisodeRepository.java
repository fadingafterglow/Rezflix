package ua.edu.ukma.springers.rezflix.repositories;

import org.springframework.data.jpa.repository.Query;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEpisodeEntity;

import java.util.Optional;
import java.util.UUID;

public interface FilmEpisodeRepository extends IRepository<FilmEpisodeEntity, UUID> {

    @Query("SELECT fe.id FROM FilmEpisodeEntity fe WHERE fe.filmDubbingId = :filmDubbingId AND fe.watchOrder = :watchOrder")
    Optional<UUID> findIdByFilmDubbingIdAndWatchOrder(int filmDubbingId, int watchOrder);
}
