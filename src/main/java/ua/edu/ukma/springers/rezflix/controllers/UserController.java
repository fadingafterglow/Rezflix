package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.UserControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.services.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerApi {

    private final UserService userService;

    @Override
    public ResponseEntity<CurrentUserInfoDto> getCurrentUserInfo() {
        throw new NotImplementedException();
    }

    @Override
    public ResponseEntity<UserListDto> getUsersByCriteria(UserCriteriaDto criteria) {
        return ResponseEntity.ok(userService.getListResponseByCriteria(criteria));
    }

    @Override
    public ResponseEntity<Integer> registerUser(RegisterUserDto dto) {
        log.info("Register user {}", dto.getUsername());
        return ResponseEntity.ok(userService.registerUser(dto));
    }

    @Override
    public ResponseEntity<Integer> createUser(CreateUserDto dto) {
        log.info("Create user {} of type {}", dto.getUsername(), dto.getType());
        return ResponseEntity.ok(userService.create(dto));
    }
}
