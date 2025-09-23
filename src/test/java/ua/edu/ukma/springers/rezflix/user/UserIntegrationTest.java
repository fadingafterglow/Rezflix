package ua.edu.ukma.springers.rezflix.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.ukma.springers.rezflix.BaseIntegrationTest;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;
import ua.edu.ukma.springers.rezflix.utils.UserRequests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ua.edu.ukma.springers.rezflix.utils.RandomUtils.*;

class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRequests requests;

    @Autowired
    private UserRepository repository;

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void userCanRegister() {
        RegisterUserDto registerUserDto = new RegisterUserDto("test", "test");
        Integer id = requests.registerUser(registerUserDto);
        assertThat(repository.findById(id))
                .isPresent()
                .get()
                .matches(user -> user.getUsername().equals(registerUserDto.getUsername()))
                .matches(user -> user.getType() == UserType.VIEWER);
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
            UserListDto response = requests.getUsersByCriteria(criteria);
            assertEquals(total, response.getTotal());
            assertThat(response.getItems())
                    .extracting(UserDto::getId)
                    .containsExactlyInAnyOrder(ids);
        }
    }
}
