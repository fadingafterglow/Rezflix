package ua.edu.ukma.springers.rezflix.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.RegisterUserDto;

import static ua.edu.ukma.springers.rezflix.utils.ApiPaths.USER_API.*;

@Component
@RequiredArgsConstructor
public class UserRequests {

    private final GeneralRequests generalRequests;

    public Integer registerUser(RegisterUserDto dto) {
        return generalRequests.create(dto, BASE + REGISTER, "", Integer.class);
    }
}
