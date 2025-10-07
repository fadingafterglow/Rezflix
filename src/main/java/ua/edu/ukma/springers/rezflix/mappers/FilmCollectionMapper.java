package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCollectionEntity;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionListDto;

@Mapper(
        config = MapperConfiguration.class,
        uses = FilmMapper.class
)
public interface FilmCollectionMapper
        extends IResponseMapper<FilmCollectionEntity, FilmCollectionDto>,
        IListResponseMapper<FilmCollectionEntity, FilmCollectionListDto> {

    @Override
    @Mapping(target = "films", source = "films")
    FilmCollectionDto toResponse(FilmCollectionEntity entity);
}
