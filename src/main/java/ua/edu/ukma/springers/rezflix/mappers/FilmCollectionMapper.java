package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmRatingDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.ShortFilmCollectionDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCollectionEntity;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionListDto;

import java.util.Map;

@Mapper(
        config = MapperConfiguration.class,
        uses = {FilmMapper.class, UserMapper.class}
)
public interface FilmCollectionMapper extends
        IShortResponseMapper<FilmCollectionEntity, ShortFilmCollectionDto>,
        IListResponseMapper<FilmCollectionEntity, FilmCollectionListDto>
{
    FilmCollectionDto toResponse(FilmCollectionEntity entity, @Context Map<Integer, FilmRatingDto> currentUserRatings);
}
