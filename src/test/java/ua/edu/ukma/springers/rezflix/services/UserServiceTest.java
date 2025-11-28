package ua.edu.ukma.springers.rezflix.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.criteria.UserCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.mappers.EnumsMapper;
import ua.edu.ukma.springers.rezflix.mappers.UserMapper;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserServiceTest extends BaseServiceTest<UserService, UserEntity, CreateUserDto, UpdateUserDto, Integer> {
    @Mock private UserRepository userRepository;
    @Mock private UserMapper mapper;
    @Mock private EnumsMapper enumsMapper;
    @Mock private SecurityUtils securityUtils;

    @Override
    protected UserService createService() {
        this.repository = userRepository;
        return new UserService(mapper, enumsMapper, securityUtils);
    }

    @Test
    @DisplayName("Should return user list DTO based on criteria")
    void getListResponseByCriteria() {
        UserCriteriaDto criteriaDto = new UserCriteriaDto();
        List<UserEntity> entities = List.of(new UserEntity());
        UserListDto listDto = new UserListDto();
        when(criteriaRepository.find(any(UserCriteria.class))).thenReturn(entities);
        when(criteriaRepository.count(any(UserCriteria.class))).thenReturn(10L);
        when(mapper.toListResponse(10L, entities)).thenReturn(listDto);
        UserListDto result = service.getListResponseByCriteria(criteriaDto);
        assertEquals(listDto, result);
    }

    @Test
    @DisplayName("Should register new user and return ID")
    void registerUser() {
        RegisterUserDto registerDto = new RegisterUserDto();
        CreateUserDto createDto = new CreateUserDto();
        int expectedId = 123;
        when(mapper.toCreateUserDto(registerDto)).thenReturn(createDto);
        when(repository.save(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity e = inv.getArgument(0);
            e.setId(expectedId);
            return e;
        });
        int resultId = service.registerUser(registerDto);
        assertEquals(expectedId, resultId);
        verify(merger).mergeForCreate(any(UserEntity.class), eq(createDto));
    }

    @Test
    @DisplayName("Should return current user info when role is standard")
    void getCurrentUserInfo() {
        UserRole role = UserRole.CONTENT_MANAGER;
        UserEntity entity = new UserEntity();
        UserDto userDto = new UserDto();
        UserRoleDto roleDto = mock(UserRoleDto.class);
        when(securityUtils.getUserRole()).thenReturn(role);
        when(securityUtils.getCurrentUser()).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(userDto);
        doReturn(roleDto).when(enumsMapper).map(role);
        CurrentUserInfoDto result = service.getCurrentUserInfo();
        assertEquals(roleDto, result.getRole());
        assertEquals(userDto, result.getInfo());
    }

    @Test
    @DisplayName("Should return info with null user part when role is Super Admin")
    void getCurrentUserInfo_ShouldReturnNullUser_WhenRoleIsSuperAdmin() {
        UserRole role = UserRole.SUPER_ADMIN;
        UserRoleDto roleDto = mock(UserRoleDto.class);
        when(securityUtils.getUserRole()).thenReturn(role);
        doReturn(roleDto).when(enumsMapper).map(role);
        CurrentUserInfoDto result = service.getCurrentUserInfo();
        assertEquals(roleDto, result.getRole());
        assertNull(result.getInfo());
        verify(securityUtils, never()).getCurrentUser();
    }

    @Test
    @DisplayName("Should return info with null user part when role is Anonymous")
    void getCurrentUserInfo_ShouldReturnNullUser_WhenRoleIsAnonymous() {
        UserRole role = UserRole.ANONYMOUS;
        UserRoleDto roleDto = mock(UserRoleDto.class);
        when(securityUtils.getUserRole()).thenReturn(role);
        doReturn(roleDto).when(enumsMapper).map(role);
        CurrentUserInfoDto result = service.getCurrentUserInfo();
        assertEquals(roleDto, result.getRole());
        assertNull(result.getInfo());
        verify(securityUtils, never()).getCurrentUser();
    }
}