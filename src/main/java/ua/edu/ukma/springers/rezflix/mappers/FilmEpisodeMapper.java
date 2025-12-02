package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.EpisodeDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.EpisodeListDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEpisodeEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.FilmEpisodeStatus;

import java.net.URI;
import java.util.UUID;

@Mapper(config = MapperConfiguration.class)
public interface FilmEpisodeMapper extends IResponseMapper<FilmEpisodeEntity, EpisodeDto>, IListResponseMapper<FilmEpisodeEntity, EpisodeListDto> {

    @Override
    @Mapping(target = "hlsLink", expression = "java(generateHlsLink(entity))")
    EpisodeDto toResponse(FilmEpisodeEntity entity);

    default URI generateHlsLink(FilmEpisodeEntity entity) {
        return entity.getStatus() == FilmEpisodeStatus.RENDERED
            ? generateHlsLink(entity.getId())
            : null;
    }

    default URI generateHlsLink(UUID episodeId) {
        return URI.create("/video/" + episodeId + "/master.m3u8");
    }
}
