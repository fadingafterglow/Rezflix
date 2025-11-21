package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmListDto;
@Mapper(config = MapperConfiguration.class)
public interface FilmMapper extends IResponseMapper<FilmEntity, FilmDto>, IListResponseMapper<FilmEntity, FilmListDto> {

    @Override
    @Mapping(target = "currentUserRating", ignore = true)
    FilmDto toResponse(FilmEntity entity);

}
