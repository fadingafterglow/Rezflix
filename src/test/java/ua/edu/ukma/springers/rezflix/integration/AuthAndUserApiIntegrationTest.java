package ua.edu.ukma.springers.rezflix.integration;

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
import org.junit.jupiter.api.BeforeEach;

class AuthAndUserApiIntegrationTest extends BaseIntegrationTest {

    @Autowired private GeneralRequests requests;
    @Autowired private IntegrationTestHelper apiHelper;

    private String superAdminToken;
    private String baseUserPath;
    private String baseAuthPath;

    @BeforeEach
    void setUp() {
        superAdminToken = apiHelper.getSuperAdminToken();
        baseUserPath = "/api/user";
        baseAuthPath = "/auth";
    }

    @Test
    void authFlow_RegisterLoginRefresh() {
        String username = "flow_" + getRandomString(8);
        String password = "password123";
        requests.create(new RegisterUserDto(username, password), baseUserPath + "/register", "", Integer.class);

        LoginResponseDto loginRes = requests.create(
                new LoginRequestDto(username, password),
                baseAuthPath + "/login", "", LoginResponseDto.class
        );
        assertThat(loginRes.getAccessToken()).isNotBlank();

        RefreshResponseDto refreshRes = requests.create(
                new RefreshRequestDto(loginRes.getRefreshToken()),
                baseAuthPath + "/refresh", "", RefreshResponseDto.class
        );
        assertThat(refreshRes.getAccessToken()).isNotBlank();
    }

    @Test
    void userApi_CRUD() {
        UserListDto list = requests.get(baseUserPath, superAdminToken, UserListDto.class);
        assertThat(list.getTotal()).isGreaterThanOrEqualTo(0);

        CreateUserDto createDto = new CreateUserDto(UserTypeDto.CONTENT_MANAGER, "cm_" + getRandomString(5), "pass");
        Integer newId = requests.create(createDto, baseUserPath, superAdminToken, Integer.class);
        assertThat(newId).isNotNull();

        String newToken = requests.getAuthToken(createDto.getUsername(), "pass");
        CurrentUserInfoDto current = requests.get(baseUserPath + "/current", "Bearer " + newToken, CurrentUserInfoDto.class);
        assertThat(current.getInfo().getId()).isEqualTo(newId);
        assertThat(current.getRole()).isEqualTo(UserRoleDto.CONTENT_MANAGER);
    }

    @Test
    void registerUser_EdgeCase_EmptyUsername() {
        RegisterUserDto dto = new RegisterUserDto("", "pass");
        requests.createFail(dto, baseUserPath + "/register", "", 400);
    }

    @Test
    void registerUser_EdgeCase_NullPassword() {
        RegisterUserDto dto = new RegisterUserDto("validUser", null);
        requests.createFail(dto, baseUserPath + "/register", "", 400);
    }

    @Test
    void registerUser_EdgeCase_DuplicateUsername() {
        String username = "dup_" + getRandomString(5);
        RegisterUserDto dto = new RegisterUserDto(username, "pass");
        requests.create(dto, baseUserPath + "/register", "", Integer.class);
        requests.createFail(dto, baseUserPath + "/register", "", 400);
    }

    @Test
    void registerUser_EdgeCase_UsernameTooLong() {
        String longUsername = getRandomString(65);
        RegisterUserDto dto = new RegisterUserDto(longUsername, "pass");
        requests.createFail(dto, baseUserPath + "/register", "", 400);
    }

    @Test
    void login_EdgeCase_WrongPassword() {
        String username = "log_" + getRandomString(5);
        requests.create(new RegisterUserDto(username, "correct"), baseUserPath + "/register", "", Integer.class);

        LoginRequestDto loginDto = new LoginRequestDto(username, "wrong");
        requests.createFail(loginDto, baseAuthPath + "/login", "", 401);
    }

    @Test
    void login_EdgeCase_NonExistentUser() {
        LoginRequestDto loginDto = new LoginRequestDto("ghost_user", "pass");
        requests.createFail(loginDto, baseAuthPath + "/login", "", 401);
    }

    @Test
    void refresh_EdgeCase_InvalidToken() {
        RefreshRequestDto dto = new RefreshRequestDto("invalid.jwt.token");
        requests.createFail(dto, baseAuthPath + "/refresh", "", 401);
    }

    @Test
    void createUser_EdgeCase_ViewerCannotCreateUser() {
        String viewerToken = "Bearer " + apiHelper.createViewerAndGetToken();
        CreateUserDto dto = new CreateUserDto(UserTypeDto.VIEWER, "new_" + getRandomString(5), "pass");
        requests.createFail(dto, baseUserPath, viewerToken, 403);
    }
}