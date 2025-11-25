package ua.edu.ukma.springers.rezflix.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.criteria.UserCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.mappers.EnumsMapper;
import ua.edu.ukma.springers.rezflix.mappers.UserMapper;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;

import java.util.List;

@Service
public class UserService extends BaseCRUDService<UserEntity, CreateUserDto, UpdateUserDto, Integer> {

    private final UserMapper mapper;
    private final EnumsMapper enumsMapper;
    private final SecurityUtils securityUtils;

    public UserService(UserMapper mapper, EnumsMapper enumsMapper, SecurityUtils securityUtils) {
        super(UserEntity.class, UserEntity::new);
        this.mapper = mapper;
        this.enumsMapper = enumsMapper;
        this.securityUtils = securityUtils;
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

    @Transactional(readOnly = true)
    public CurrentUserInfoDto getCurrentUserInfo() {
        UserRole role = securityUtils.getUserRole();
        UserDto user = switch (role) {
            case SUPER_ADMIN, ANONYMOUS -> null;
            default -> mapper.toResponse(securityUtils.getCurrentUser());
        };
        return new CurrentUserInfoDto(enumsMapper.map(role), user);
    }
}
