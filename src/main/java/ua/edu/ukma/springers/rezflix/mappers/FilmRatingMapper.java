package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmRatingEntity;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmRatingDto;

@Mapper(config = MapperConfiguration.class)
public interface FilmRatingMapper extends IResponseMapper<FilmRatingEntity, FilmRatingDto>{

}
