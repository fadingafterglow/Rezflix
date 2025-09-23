package ua.edu.ukma.springers.rezflix.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateUserDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.RegisterUserDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UserDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UserListDto;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;

@Mapper(config = MapperConfiguration.class)
public interface UserMapper extends IResponseMapper<UserEntity, UserDto>, IListResponseMapper<UserEntity, UserListDto> {
    @Mapping(target = "type", constant = "VIEWER")
    CreateUserDto toCreateUserDto(RegisterUserDto registerUserDto);
}
