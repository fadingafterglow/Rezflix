package ua.edu.ukma.springers.rezflix.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;

import static ua.edu.ukma.springers.rezflix.utils.ApiPaths.USER_API.*;

@Component
@RequiredArgsConstructor
public class UserRequests {

    private final GeneralRequests generalRequests;

    public Integer registerUser(RegisterUserDto dto) {
        return generalRequests.create(dto, BASE + REGISTER, "", Integer.class);
    }

    public UserListDto getUsersByCriteria(UserCriteriaDto criteria) {
        return generalRequests.getByCriteria(BASE, criteria, "", UserListDto.class);
    }

    public Integer createUser(CreateUserDto dto, String authToken) {
        return generalRequests.create(dto, BASE, authToken, Integer.class);
    }

    public void createUserFail(CreateUserDto dto, String authToken, int expectedStatus) {
        generalRequests.createFail(dto, BASE, authToken, expectedStatus);
    }

    public CurrentUserInfoDto getCurrentUserInfo(String authToken) {
        return generalRequests.get(BASE + CURRENT, authToken, CurrentUserInfoDto.class);
    }
}
