package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.EpisodeDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.EpisodeListDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEpisodeEntity;

@Mapper(config = MapperConfiguration.class)
public interface FilmEpisodeMapper extends IResponseMapper<FilmEpisodeEntity, EpisodeDto>, IListResponseMapper<FilmEpisodeEntity, EpisodeListDto> {

    @Override
    @Mapping(target = "hlsLink", ignore = true)
    EpisodeDto toResponse(FilmEpisodeEntity entity);
}
