package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Mapper;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CommentDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CommentListDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCommentEntity;

@Mapper(config = MapperConfiguration.class, uses = {UserMapper.class, FilmRatingMapper.class})
public interface FilmCommentMapper extends IResponseMapper<FilmCommentEntity, CommentDto>, IListResponseMapper<FilmCommentEntity, CommentListDto> {}
