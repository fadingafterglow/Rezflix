package ua.edu.ukma.springers.rezflix.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.edu.ukma.springers.rezflix.configuration.WebSecurityConfiguration;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmDto;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;
import ua.edu.ukma.springers.rezflix.security.CustomUserDetailsService;
import ua.edu.ukma.springers.rezflix.security.JWTService;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;
import ua.edu.ukma.springers.rezflix.services.FilmRecommendationsService;
import ua.edu.ukma.springers.rezflix.utils.ApiPaths;
import ua.edu.ukma.springers.rezflix.utils.DefaultMessageResolver;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmRecommendationsController.class)
@ActiveProfiles("test")
@Import({
        WebSecurityConfiguration.class,
        CustomUserDetailsService.class,
        JWTService.class,
        SecurityUtils.class,
        DefaultMessageResolver.class
})
class FilmRecommendationsControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private FilmRecommendationsService recommendationService;
    @MockitoBean
    private UserRepository userRepository;

    private String viewerToken;
    private final int VIEWER_ID = 10;

    @BeforeEach
    void setUp() {
        String viewerUsername = "viewer";
        UserEntity viewer = new UserEntity();
        viewer.setId(VIEWER_ID);
        viewer.setUsername(viewerUsername);
        viewer.setPasswordHash(passwordEncoder.encode("password"));
        viewer.setType(UserType.VIEWER);

        when(userRepository.findByUsername(viewerUsername)).thenReturn(Optional.of(viewer));

        this.viewerToken = "Bearer " + jwtService.generateAccessToken(viewerUsername, UserRole.VIEWER.name());
    }

    @Test
    void getRecommendations_Viewer_ReturnsList() throws Exception {
        FilmDto rec1 = new FilmDto();
        rec1.setId(101);
        rec1.setTitle("Recommended Sci-Fi");

        FilmDto rec2 = new FilmDto();
        rec2.setId(102);
        rec2.setTitle("Recommended Drama");

        when(recommendationService.getRecommendationsForUser(VIEWER_ID)).thenReturn(List.of(rec1, rec2));

        mvc.perform(get(ApiPaths.FILM_RECOMENDATION_API.BASE)
                        .header(HttpHeaders.AUTHORIZATION, viewerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Recommended Sci-Fi"))
                .andExpect(jsonPath("$[1].title").value("Recommended Drama"));
    }

    @Test
    void getRecommendations_Anonymous_ReturnsUnauthorized() throws Exception {
        mvc.perform(get(ApiPaths.FILM_RECOMENDATION_API.BASE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getRecommendations_EmptyList_ReturnsOk() throws Exception {
        when(recommendationService.getRecommendationsForUser(VIEWER_ID)).thenReturn(List.of());

        mvc.perform(get(ApiPaths.FILM_RECOMENDATION_API.BASE)
                        .header(HttpHeaders.AUTHORIZATION, viewerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}