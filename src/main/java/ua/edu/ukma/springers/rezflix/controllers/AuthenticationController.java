package ua.edu.ukma.springers.rezflix.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.AuthenticationControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.LoginRequestDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.LoginResponseDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.RefreshRequestDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.RefreshResponseDto;

@RestController
public class AuthenticationController implements AuthenticationControllerApi {

    @Override
    public ResponseEntity<LoginResponseDto> login(LoginRequestDto loginRequestDto) {
        throw new IllegalStateException("Should be handled by filter");
    }

    @Override
    public ResponseEntity<RefreshResponseDto> refresh(RefreshRequestDto refreshRequestDto) {
        throw new IllegalStateException("Should be handled by filter");
    }
}
