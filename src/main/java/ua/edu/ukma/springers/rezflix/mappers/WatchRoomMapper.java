package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.ukma.springers.rezflix.controllers.websocket.model.WatchRoomStateDto;
import ua.edu.ukma.springers.rezflix.domain.entities.WatchRoomEntity;

@Mapper(config = MapperConfiguration.class)
public interface WatchRoomMapper extends IResponseMapper<WatchRoomEntity, WatchRoomStateDto> {

    @Override
    @Mapping(target = "isPaused", source = "paused")
    WatchRoomStateDto toResponse(WatchRoomEntity entity);
}
