package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Mapper;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.DubbingDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.DubbingListDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmDubbingEntity;

@Mapper(config = MapperConfiguration.class)
public interface FilmDubbingMapper extends IResponseMapper<FilmDubbingEntity, DubbingDto>, IListResponseMapper<FilmDubbingEntity, DubbingListDto> {}
