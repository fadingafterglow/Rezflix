package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CommentDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CommentListDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCommentEntity;

@Mapper(config = MapperConfiguration.class, uses = UserMapper.class)
public interface FilmCommentMapper extends IResponseMapper<FilmCommentEntity, CommentDto>, IListResponseMapper<FilmCommentEntity, CommentListDto> {
    @Override
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "authorRating", ignore = true)
    @Mapping(target = "filmId", source = "film.id")
    @Mapping(target = "author", source = "user")
    CommentDto toResponse(FilmCommentEntity entity);
}
