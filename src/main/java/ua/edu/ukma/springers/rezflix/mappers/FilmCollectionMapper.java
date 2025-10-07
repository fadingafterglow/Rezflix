package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Mapper;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.ShortFilmCollectionDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCollectionEntity;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionListDto;

@Mapper(
        config = MapperConfiguration.class,
        uses = {FilmMapper.class, UserMapper.class}
)
public interface FilmCollectionMapper extends
        IResponseMapper<FilmCollectionEntity, FilmCollectionDto>,
        IShortResponseMapper<FilmCollectionEntity, ShortFilmCollectionDto>,
        IListResponseMapper<FilmCollectionEntity, FilmCollectionListDto>
{}
