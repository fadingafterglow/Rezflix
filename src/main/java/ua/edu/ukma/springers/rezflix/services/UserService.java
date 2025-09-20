package ua.edu.ukma.springers.rezflix.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateUserDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.RegisterUserDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpdateUserDto;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.mappers.UserMapper;
import ua.edu.ukma.springers.rezflix.mergers.IMerger;
import ua.edu.ukma.springers.rezflix.repositories.IRepository;
import ua.edu.ukma.springers.rezflix.validators.IValidator;

@Service
public class UserService extends BaseCRUDService<UserEntity, CreateUserDto, UpdateUserDto, Integer> {

    private final UserMapper mapper;

    public UserService(IRepository<UserEntity, Integer> repository, IValidator<UserEntity> validator,
                       IMerger<UserEntity, CreateUserDto, UpdateUserDto> merger, UserMapper mapper) {
        super(repository, validator, merger, UserEntity.class, UserEntity::new);
        this.mapper = mapper;
    }

    @Transactional
    public int registerUser(RegisterUserDto registerUserDto) {
        return create(mapper.toCreateUserDto(registerUserDto));
    }
}
