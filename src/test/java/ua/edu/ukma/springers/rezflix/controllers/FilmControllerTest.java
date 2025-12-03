package ua.edu.ukma.springers.rezflix.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.edu.ukma.springers.rezflix.configuration.WebSecurityConfiguration;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCriteriaDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmListDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpsertFilmDto;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;
import ua.edu.ukma.springers.rezflix.security.CustomUserDetailsService;
import ua.edu.ukma.springers.rezflix.security.JWTService;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;
import ua.edu.ukma.springers.rezflix.services.FilmService;
import ua.edu.ukma.springers.rezflix.utils.ApiPaths;
import ua.edu.ukma.springers.rezflix.utils.DefaultMessageResolver;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
@ActiveProfiles("test")
@Import({
        WebSecurityConfiguration.class,
        CustomUserDetailsService.class,
        JWTService.class,
        SecurityUtils.class,
        DefaultMessageResolver.class
})
class FilmControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private FilmService filmService;
    @MockitoBean
    private UserRepository userRepository;

    private String contentManagerToken;
    private String viewerToken;

    @BeforeEach
    void setUp() {
        String cmUsername = "manager";
        UserEntity contentManager = new UserEntity();
        contentManager.setId(10);
        contentManager.setUsername(cmUsername);
        contentManager.setPasswordHash(passwordEncoder.encode("password"));
        contentManager.setType(UserType.CONTENT_MANAGER);

        String viewerUsername = "viewer";
        UserEntity viewer = new UserEntity();
        viewer.setId(20);
        viewer.setUsername(viewerUsername);
        viewer.setPasswordHash(passwordEncoder.encode("password"));
        viewer.setType(UserType.VIEWER);

        when(userRepository.findByUsername(cmUsername)).thenReturn(Optional.of(contentManager));
        when(userRepository.findByUsername(viewerUsername)).thenReturn(Optional.of(viewer));

        this.contentManagerToken = "Bearer " + jwtService.generateAccessToken(cmUsername, UserRole.CONTENT_MANAGER.name());
        this.viewerToken = "Bearer " + jwtService.generateAccessToken(viewerUsername, UserRole.VIEWER.name());
    }

    @Test
    void getFilm_PublicEndpoint_ReturnsFilm() throws Exception {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(1);
        filmDto.setTitle("Inception");

        when(filmService.getResponseById(1)).thenReturn(filmDto);

        mvc.perform(get(ApiPaths.FILM_API.BASE + ApiPaths.FILM_API.ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    void getFilmsByCriteria_PublicEndpoint_ReturnsList() throws Exception {
        FilmListDto listDto = new FilmListDto();
        listDto.setItems(List.of());
        listDto.setTotal(0L);

        when(filmService.getListResponseByCriteria(any(FilmCriteriaDto.class))).thenReturn(listDto);

        mvc.perform(get(ApiPaths.FILM_API.BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void createFilm_ContentManager_ReturnsId() throws Exception {
        UpsertFilmDto dto = new UpsertFilmDto();
        dto.setTitle("New Film");
        dto.setDescription("Description");

        when(filmService.create(any(UpsertFilmDto.class))).thenReturn(100);

        mvc.perform(post(ApiPaths.FILM_API.BASE)
                        .header(HttpHeaders.AUTHORIZATION, contentManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(100));
    }

    @Test
    void createFilm_Viewer_ReturnsForbidden() throws Exception {
        UpsertFilmDto dto = new UpsertFilmDto();
        dto.setTitle("Hacked Film");

        mvc.perform(post(ApiPaths.FILM_API.BASE)
                        .header(HttpHeaders.AUTHORIZATION, viewerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createFilm_Anonymous_ReturnsUnauthorized() throws Exception {
        UpsertFilmDto dto = new UpsertFilmDto();
        dto.setTitle("Anon Film");

        mvc.perform(post(ApiPaths.FILM_API.BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateFilm_ContentManager_ReturnsNoContent() throws Exception {
        Integer filmId = 1;
        UpsertFilmDto dto = new UpsertFilmDto();
        dto.setTitle("Updated Film");
        dto.setDescription("Updated Film description`");

        when(filmService.update(eq(filmId), any(UpsertFilmDto.class))).thenReturn(true);

        mvc.perform(put(ApiPaths.FILM_API.BASE + ApiPaths.FILM_API.ID, filmId)
                        .header(HttpHeaders.AUTHORIZATION, contentManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteFilm_ContentManager_ReturnsNoContent() throws Exception {
        Integer filmId = 1;
        doNothing().when(filmService).delete(filmId);

        mvc.perform(delete(ApiPaths.FILM_API.BASE + ApiPaths.FILM_API.ID, filmId)
                        .header(HttpHeaders.AUTHORIZATION, contentManagerToken))
                .andExpect(status().isNoContent());
    }
}