package ua.edu.ukma.springers.rezflix.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.edu.ukma.springers.rezflix.configuration.WebSecurityConfiguration;
import ua.edu.ukma.springers.rezflix.controllers.AuthenticationController;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.LoginRequestDto;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;
import ua.edu.ukma.springers.rezflix.utils.ApiPaths;
import ua.edu.ukma.springers.rezflix.utils.DefaultMessageResolver;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@ActiveProfiles("test")
@Import({
    WebSecurityConfiguration.class, CustomUserDetailsService.class, JWTService.class, SecurityUtils.class,
    DefaultMessageResolver.class
})
public class AuthenticationTest {

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${security.super-admin.login}")
    private String superAdminLogin;
    @Value("${security.super-admin.password}")
    private String superAdminPassword;

    @Test
    void superAdminCanLogin() throws Exception {
        mvc
            .perform(
                post(ApiPaths.AUTH_API.BASE + ApiPaths.AUTH_API.LOGIN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginRequestOf(superAdminLogin, superAdminPassword))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void nonExistentUserCannotLogin() throws Exception {
        mvc
            .perform(
                post(ApiPaths.AUTH_API.BASE + ApiPaths.AUTH_API.LOGIN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginRequestOf("user", "password"))
            )
            .andExpect(status().is(HttpServletResponse.SC_UNAUTHORIZED))
            .andExpect(jsonPath("$.message").value("error.application.unauthenticated"));
    }

    @Test
    void existentUserWithWrongPasswordCannotLogin() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setType(UserType.VIEWER);
        userEntity.setUsername("user");
        userEntity.setPasswordHash(passwordEncoder.encode("correct_password"));
        when(userRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        mvc
            .perform(
                post(ApiPaths.AUTH_API.BASE + ApiPaths.AUTH_API.LOGIN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginRequestOf(userEntity.getUsername(), "incorrect_password"))
            )
            .andExpect(status().is(HttpServletResponse.SC_UNAUTHORIZED))
            .andExpect(jsonPath("$.message").value("error.application.unauthenticated"));
    }

    @Test
    void existentUserWithCorrectPasswordCanLogin() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setType(UserType.VIEWER);
        userEntity.setUsername("user");
        userEntity.setPasswordHash(passwordEncoder.encode("password"));
        when(userRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        mvc
            .perform(
                post(ApiPaths.AUTH_API.BASE + ApiPaths.AUTH_API.LOGIN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginRequestOf(userEntity.getUsername(), "password"))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists());
    }

    @SneakyThrows
    private String loginRequestOf(String username, String password) {
        LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);
        return objectMapper.writeValueAsString(loginRequestDto);
    }
}
