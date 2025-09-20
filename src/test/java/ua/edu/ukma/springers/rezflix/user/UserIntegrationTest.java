package ua.edu.ukma.springers.rezflix.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.ukma.springers.rezflix.BaseIntegrationTest;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.RegisterUserDto;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;
import ua.edu.ukma.springers.rezflix.utils.UserRequests;

import static org.assertj.core.api.Assertions.assertThat;

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
}
