package ua.edu.ukma.springers.rezflix.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateWatchRoomDto;
import ua.edu.ukma.springers.rezflix.controllers.websocket.model.WatchRoomDto;
import ua.edu.ukma.springers.rezflix.controllers.websocket.model.WatchRoomStateDto;
import ua.edu.ukma.springers.rezflix.domain.entities.WatchRoomEntity;
import ua.edu.ukma.springers.rezflix.exceptions.NotFoundException;
import ua.edu.ukma.springers.rezflix.mappers.WatchRoomMapper;
import ua.edu.ukma.springers.rezflix.mergers.WatchRoomMerger;
import ua.edu.ukma.springers.rezflix.repositories.WatchRoomRepository;
import ua.edu.ukma.springers.rezflix.validators.WatchRoomValidator;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WatchRoomServiceTest {

    @Mock private WatchRoomRepository repository;
    @Mock private WatchRoomMerger merger;
    @Mock private WatchRoomValidator validator;
    @Mock private WatchRoomMapper mapper;

    @InjectMocks private WatchRoomService service;

    @Test
    void getWatchRoomInfo_Found() {
        UUID roomId = UUID.randomUUID();
        WatchRoomEntity entity = new WatchRoomEntity();
        WatchRoomDto dto = new WatchRoomDto(false, 0, 1, UUID.randomUUID(), null);

        when(repository.findById(roomId)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(dto);

        assertEquals(dto, service.getWatchRoomInfo(roomId));
    }

    @Test
    void getWatchRoomInfo_NotFound() {
        UUID roomId = UUID.randomUUID();
        when(repository.findById(roomId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getWatchRoomInfo(roomId));
    }

    @Test
    void createWatchRoom() {
        CreateWatchRoomDto dto = new CreateWatchRoomDto();
        UUID generatedId = UUID.randomUUID();

        when(repository.save(any(WatchRoomEntity.class))).thenAnswer(inv -> {
            WatchRoomEntity e = inv.getArgument(0);
            e.setRoomId(generatedId);
            return e;
        });

        UUID resultId = service.createWatchRoom(dto);

        assertEquals(generatedId, resultId);
        verify(merger).mergeForCreate(any(WatchRoomEntity.class), eq(dto));
        verify(validator).validForCreate(any(WatchRoomEntity.class));
    }

    @Test
    void syncWatchRoomState() {
        UUID roomId = UUID.randomUUID();
        WatchRoomEntity entity = new WatchRoomEntity();
        WatchRoomStateDto stateDto = new WatchRoomStateDto(true, 1000L);

        when(repository.findById(roomId)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toShortResponse(entity)).thenReturn(stateDto);

        WatchRoomStateDto result = service.syncWatchRoomState(roomId, stateDto);

        assertEquals(stateDto, result);
        verify(merger).mergeForUpdate(entity, stateDto);
    }
}