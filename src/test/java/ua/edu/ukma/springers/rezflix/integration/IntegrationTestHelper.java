package ua.edu.ukma.springers.rezflix.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateUserDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UserTypeDto;
import ua.edu.ukma.springers.rezflix.utils.GeneralRequests;

import static ua.edu.ukma.springers.rezflix.utils.RandomUtils.getRandomString;

@Component
public class IntegrationTestHelper {

    @Autowired
    private GeneralRequests requests;

    public String getSuperAdminToken() {
        return requests.getSuperAdminAuthToken();
    }

    public String createContentManagerAndGetToken() {
        return createUserAndGetToken(UserTypeDto.CONTENT_MANAGER);
    }

    public String createModeratorAndGetToken() {
        return createUserAndGetToken(UserTypeDto.MODERATOR);
    }

    public String createViewerAndGetToken() {
        return createUserAndGetToken(UserTypeDto.VIEWER);
    }

    private String createUserAndGetToken(UserTypeDto type) {
        String username = "user_" + getRandomString(8);
        String password = "password";
        CreateUserDto dto = new CreateUserDto(type, username, password);
        requests.create(dto, "/api/user", getSuperAdminToken(), Integer.class);
        return requests.getAuthToken(username, password);
    }
}