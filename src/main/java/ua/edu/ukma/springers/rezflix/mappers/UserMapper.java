package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;

@Mapper(config = MapperConfiguration.class)
public interface UserMapper extends
        IResponseMapper<UserEntity, UserDto>,
        IShortResponseMapper<UserEntity, ShortUserDto>,
        IListResponseMapper<UserEntity, UserListDto>
{
    @Mapping(target = "type", constant = "VIEWER")
    CreateUserDto toCreateUserDto(RegisterUserDto registerUserDto);
}
