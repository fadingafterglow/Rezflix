package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Mapper;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmInfoLookupResultDto;
import ua.edu.ukma.springers.rezflix.services.FilmInfoLookupService;

@Mapper(config = MapperConfiguration.class)
public interface FilmInfoLookupMapper {

    FilmInfoLookupResultDto map(FilmInfoLookupService.FilmInfoLookupApiResponse response);
}
