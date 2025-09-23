package ua.edu.ukma.springers.rezflix.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.criteria.UserCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.mappers.EnumsMapper;
import ua.edu.ukma.springers.rezflix.mappers.UserMapper;
import ua.edu.ukma.springers.rezflix.mergers.IMerger;
import ua.edu.ukma.springers.rezflix.repositories.CriteriaRepository;
import ua.edu.ukma.springers.rezflix.repositories.IRepository;
import ua.edu.ukma.springers.rezflix.validators.IValidator;

import java.util.List;

@Service
public class UserService extends BaseCRUDService<UserEntity, CreateUserDto, UpdateUserDto, Integer> {

    private final UserMapper mapper;
    private final EnumsMapper enumsMapper;

    public UserService(IRepository<UserEntity, Integer> repository, CriteriaRepository criteriaRepository,
                       IValidator<UserEntity> validator, IMerger<UserEntity, CreateUserDto, UpdateUserDto> merger,
                       UserMapper mapper, EnumsMapper enumsMapper) {
        super(repository, criteriaRepository, validator, merger, UserEntity.class, UserEntity::new);
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
