package ua.edu.ukma.springers.rezflix.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.criteria.UserCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.mappers.EnumsMapper;
import ua.edu.ukma.springers.rezflix.mappers.UserMapper;

import java.util.List;

@Service
public class UserService extends BaseCRUDService<UserEntity, CreateUserDto, UpdateUserDto, Integer> {

    private final UserMapper mapper;
    private final EnumsMapper enumsMapper;

    public UserService(UserMapper mapper, EnumsMapper enumsMapper) {
        super(UserEntity.class, UserEntity::new);
        this.mapper = mapper;
        this.enumsMapper = enumsMapper;
    }

    @Transactional(readOnly = true)
    public UserListDto getListResponseByCriteria(UserCriteriaDto criteriaDto) {
        UserCriteria criteria = new UserCriteria(criteriaDto, enumsMapper);
        List<UserEntity> entities = getList(criteria);
        long total = count(criteria);
        return mapper.toListResponse(total, entities);
    }

    @Transactional
    public int registerUser(RegisterUserDto registerUserDto) {
        return create(mapper.toCreateUserDto(registerUserDto));
    }
}
