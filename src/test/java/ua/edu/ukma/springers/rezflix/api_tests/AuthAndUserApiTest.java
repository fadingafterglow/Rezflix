package ua.edu.ukma.springers.rezflix.api_tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.ukma.springers.rezflix.BaseIntegrationTest;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateUserDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CurrentUserInfoDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.LoginRequestDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.LoginResponseDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.RefreshRequestDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.RefreshResponseDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.RegisterUserDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UserListDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UserTypeDto;
import ua.edu.ukma.springers.rezflix.utils.GeneralRequests;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UserRoleDto;
import static org.assertj.core.api.Assertions.assertThat;
import static ua.edu.ukma.springers.rezflix.utils.RandomUtils.getRandomString;

class AuthAndUserApiTest extends BaseIntegrationTest {

    @Autowired private GeneralRequests requests;
    @Autowired private ApiTestHelper apiHelper;

    @Test
    void authFlow_RegisterLoginRefresh() {
        String username = "flow_user_" + getRandomString(5);
        String password = "password123";
        requests.create(new RegisterUserDto(username, password), "/api/user/register", "", Integer.class);
        LoginResponseDto loginRes = requests.create(
                new LoginRequestDto(username, password),
                "/auth/login", "", LoginResponseDto.class
        );
        assertThat(loginRes.getAccessToken()).isNotBlank();
        RefreshResponseDto refreshRes = requests.create(
                new RefreshRequestDto(loginRes.getRefreshToken()),
                "/auth/refresh", "", RefreshResponseDto.class
        );
        assertThat(refreshRes.getAccessToken()).isNotBlank();
    }

    @Test
    void userApi_CRUD() {
        String token = apiHelper.getSuperAdminToken();
        UserListDto list = requests.get("/api/user", token, UserListDto.class);
        assertThat(list.getTotal()).isGreaterThanOrEqualTo(0);
        CreateUserDto createDto = new CreateUserDto(UserTypeDto.CONTENT_MANAGER, "cm_" + getRandomString(5), "pass");
        Integer newId = requests.create(createDto, "/api/user", token, Integer.class);
        assertThat(newId).isNotNull();
        String newToken = requests.getAuthToken(createDto.getUsername(), "pass");
        CurrentUserInfoDto current = requests.get("/api/user/current", "Bearer " + newToken, CurrentUserInfoDto.class);
        assertThat(current.getInfo().getId()).isEqualTo(newId);
        assertThat(current.getRole()).isEqualTo(UserRoleDto.CONTENT_MANAGER);
    }
}