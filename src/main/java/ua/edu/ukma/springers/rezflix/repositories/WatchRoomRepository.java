package ua.edu.ukma.springers.rezflix.repositories;

import org.springframework.data.repository.CrudRepository;
import ua.edu.ukma.springers.rezflix.domain.entities.WatchRoomEntity;

import java.util.UUID;

public interface WatchRoomRepository extends CrudRepository<WatchRoomEntity, UUID> {}