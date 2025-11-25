package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmRatingDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEntity;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmListDto;

import java.util.List;
import java.util.Map;

@Mapper(config = MapperConfiguration.class)
public interface FilmMapper {

    @Mapping(target = "currentUserRating", expression = "java(currentUserRatings.get(entity.getId()))")
    FilmDto toResponse(FilmEntity entity, @Context Map<Integer, FilmRatingDto> currentUserRatings);

    FilmListDto toListResponse(long total, List<FilmEntity> items, @Context Map<Integer, FilmRatingDto> currentUserRatings);

}
