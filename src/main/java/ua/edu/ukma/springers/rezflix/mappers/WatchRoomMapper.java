package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.ukma.springers.rezflix.controllers.websocket.model.WatchRoomDto;
import ua.edu.ukma.springers.rezflix.controllers.websocket.model.WatchRoomStateDto;
import ua.edu.ukma.springers.rezflix.domain.entities.WatchRoomEntity;

@Mapper(config = MapperConfiguration.class, uses = FilmEpisodeMapper.class)
public interface WatchRoomMapper extends IResponseMapper<WatchRoomEntity, WatchRoomDto>, IShortResponseMapper<WatchRoomEntity, WatchRoomStateDto> {

    @Override
    @Mapping(target = "isPaused", source = "paused")
    @Mapping(target = "hlsLink", source = "entity.episodeId")
    WatchRoomDto toResponse(WatchRoomEntity entity);

    @Override
    @Mapping(target = "isPaused", source = "paused")
    WatchRoomStateDto toShortResponse(WatchRoomEntity entity);
}
