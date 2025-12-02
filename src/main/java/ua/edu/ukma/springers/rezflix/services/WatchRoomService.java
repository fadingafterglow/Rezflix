package ua.edu.ukma.springers.rezflix.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateWatchRoomDto;
import ua.edu.ukma.springers.rezflix.controllers.websocket.model.WatchRoomDto;
import ua.edu.ukma.springers.rezflix.controllers.websocket.model.WatchRoomStateDto;
import ua.edu.ukma.springers.rezflix.domain.entities.WatchRoomEntity;
import ua.edu.ukma.springers.rezflix.exceptions.NotFoundException;
import ua.edu.ukma.springers.rezflix.mappers.WatchRoomMapper;
import ua.edu.ukma.springers.rezflix.mergers.WatchRoomMerger;
import ua.edu.ukma.springers.rezflix.repositories.WatchRoomRepository;
import ua.edu.ukma.springers.rezflix.validators.WatchRoomValidator;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WatchRoomService {

    private final WatchRoomRepository repository;
    private final WatchRoomMerger merger;
    private final WatchRoomValidator validator;
    private final WatchRoomMapper mapper;

    public WatchRoomDto getWatchRoomInfo(UUID roomId) {
        return mapper.toResponse(getOrThrowNotFound(roomId));
    }

    public UUID createWatchRoom(CreateWatchRoomDto createWatchRoomDto) {
        WatchRoomEntity entity = new WatchRoomEntity();
        merger.mergeForCreate(entity, createWatchRoomDto);
        validator.validForCreate(entity);
        return repository.save(entity).getRoomId();
    }

    public WatchRoomStateDto syncWatchRoomState(UUID roomId, WatchRoomStateDto watchRoomStateDto) {
        WatchRoomEntity entity = getOrThrowNotFound(roomId);
        merger.mergeForUpdate(entity, watchRoomStateDto);
        return mapper.toShortResponse(repository.save(entity));
    }

    private WatchRoomEntity getOrThrowNotFound(UUID roomId) {
        return repository.findById(roomId).orElseThrow(() -> new NotFoundException(WatchRoomEntity.class, "id: " + roomId));
    }
}
