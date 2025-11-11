package ua.edu.ukma.springers.rezflix.user;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.ukma.springers.rezflix.BaseIntegrationTest;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;
import ua.edu.ukma.springers.rezflix.utils.GeneralRequests;
import ua.edu.ukma.springers.rezflix.utils.UserRequests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static ua.edu.ukma.springers.rezflix.utils.RandomUtils.*;

class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRequests userRequests;
    @Autowired
    private GeneralRequests generalRequests;

    @Autowired
    private UserRepository repository;

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void userCanRegister() {
        RegisterUserDto registerUserDto = new RegisterUserDto("test", "test");
        Integer id = userRequests.registerUser(registerUserDto);
        assertThat(repository.findById(id))
                .isPresent()
                .get()
                .matches(user -> user.getUsername().equals(registerUserDto.getUsername()))
                .matches(user -> user.getType() == UserType.VIEWER);
    }

    @ParameterizedTest
    @EnumSource(UserTypeDto.class)
    void superAdminCanCreateUsersOfAnyType(UserTypeDto userType) {
        String superAdminToken = generalRequests.getSuperAdminAuthToken();
        CreateUserDto createUserDto = new CreateUserDto(userType, getRandomString(10), getRandomString(10));

        Integer userId = userRequests.createUser(createUserDto, superAdminToken);

        assertThat(repository.findById(userId))
                .isPresent()
                .get()
                .matches(user -> user.getUsername().equals(createUserDto.getUsername()))
                .matches(user -> user.getType().name().equals(userType.name()));
    }

    @ParameterizedTest
    @EnumSource(UserTypeDto.class)
    void otherRolesCannotCreateUsers(UserTypeDto userType) {
        String superAdminToken = generalRequests.getSuperAdminAuthToken();
        CreateUserDto createUserDto = new CreateUserDto(userType, "user", "password");
        userRequests.createUser(createUserDto, superAdminToken);
        String userToken = generalRequests.getAuthToken("user", "password");

        for (UserTypeDto newUserType : UserTypeDto.values()) {
            CreateUserDto dto = new CreateUserDto(newUserType, getRandomString(10), getRandomString(10));
            userRequests.createUserFail(dto, userToken, HttpServletResponse.SC_FORBIDDEN);
        }

        assertEquals(1, repository.count());
    }

    @ParameterizedTest
    @EnumSource(UserTypeDto.class)
    void anonymousCannotCreateUsers(UserTypeDto userType) {
        CreateUserDto dto = new CreateUserDto(userType, getRandomString(10), getRandomString(10));
        userRequests.createUserFail(dto, "", HttpServletResponse.SC_UNAUTHORIZED);
        assertEquals(0, repository.count());
    }

    @Test
    void superAdminCanViewCurrentUserInfo() {
        String superAdminToken = generalRequests.getSuperAdminAuthToken();

        CurrentUserInfoDto response = userRequests.getCurrentUserInfo(superAdminToken);

        assertEquals(UserRoleDto.SUPER_ADMIN, response.getRole());
        assertNull(response.getInfo());
    }

    @ParameterizedTest
    @EnumSource(UserTypeDto.class)
    void otherRolesCanViewCurrentUserInfo(UserTypeDto userType) {
        String superAdminToken = generalRequests.getSuperAdminAuthToken();
        CreateUserDto createUserDto = new CreateUserDto(userType, "user", "password");
        Integer userId = userRequests.createUser(createUserDto, superAdminToken);
        String userToken = generalRequests.getAuthToken("user", "password");

        CurrentUserInfoDto response = userRequests.getCurrentUserInfo(userToken);

        assertEquals(userType.name(), response.getRole().name());
        assertNotNull(response.getInfo());
        assertEquals(userId, response.getInfo().getId());
        assertEquals(userType, response.getInfo().getType());
    }

    @Test
    void anonymousCanViewCurrentUserInfo() {
        CurrentUserInfoDto response = userRequests.getCurrentUserInfo("");

        assertEquals(UserRoleDto.ANONYMOUS, response.getRole());
        assertNull(response.getInfo());
    }

    @Nested
    class CriteriaTests {

        private int userId_1;
        private int userId_2;
        private int userId_3;

        @BeforeEach
        void createUsers() {
            userId_1 = createUser("user1", UserType.VIEWER);
            userId_2 = createUser("username", UserType.MODERATOR);
            userId_3 = createUser("hahaha", UserType.CONTENT_MANAGER);
        }

        @Test
        void anybodyCanViewUsersByCriteria() {
            UserCriteriaDto criteria = new UserCriteriaDto();
            assertListResponse(criteria, 3, userId_1, userId_2, userId_3);
        }

        @Test
        void canFilterUsersByQuery() {
            UserCriteriaDto criteria = new UserCriteriaDto();
            criteria.setQuery("uSeR");
            assertListResponse(criteria, 2, userId_1, userId_2);

            criteria = new UserCriteriaDto();
            criteria.setQuery("h");
            assertListResponse(criteria, 1, userId_3);
        }

        @Test
        void canFilterUsersByType() {
            UserCriteriaDto criteria = new UserCriteriaDto();
            criteria.setType(UserTypeDto.MODERATOR);
            assertListResponse(criteria, 1, userId_2);
        }

        private int createUser(String username, UserType type) {
            UserEntity userEntity = new UserEntity();
            userEntity.setType(type);
            userEntity.setUsername(username);
            userEntity.setPasswordHash(getRandomString(10));
            return repository.save(userEntity).getId();
        }

        private void assertListResponse(UserCriteriaDto criteria, long total, Integer... ids) {
            UserListDto response = userRequests.getUsersByCriteria(criteria);
            assertEquals(total, response.getTotal());
            assertThat(response.getItems())
                    .extracting(UserDto::getId)
                    .containsExactlyInAnyOrder(ids);
        }
    }
}
