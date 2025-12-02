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
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;
import ua.edu.ukma.springers.rezflix.exceptions.NotFoundException;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;
import ua.edu.ukma.springers.rezflix.security.CustomUserDetailsService;
import ua.edu.ukma.springers.rezflix.security.JWTService;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;
import ua.edu.ukma.springers.rezflix.services.FilmDubbingService;
import ua.edu.ukma.springers.rezflix.utils.ApiPaths;
import ua.edu.ukma.springers.rezflix.utils.DefaultMessageResolver;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmDubbingController.class)
@ActiveProfiles("test")
@Import({
        WebSecurityConfiguration.class,
        CustomUserDetailsService.class,
        JWTService.class,
        SecurityUtils.class,
        DefaultMessageResolver.class
})
class FilmDubbingControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private FilmDubbingService dubbingService;
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
    void getDubbing_PublicEndpoint_ReturnsDubbing() throws Exception {
        DubbingDto dto = new DubbingDto();
        dto.setId(1);
        dto.setName("English Original");

        when(dubbingService.getResponseById(1)).thenReturn(dto);

        mvc.perform(get(ApiPaths.FILM_DUBBING_API.BASE + ApiPaths.FILM_DUBBING_API.ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("English Original"));
    }

    @Test
    void createDubbing_MissingName_ReturnsBadRequest() throws Exception {
        CreateDubbingDto dto = new CreateDubbingDto();
        dto.setFilmId(1);

        mvc.perform(post(ApiPaths.FILM_DUBBING_API.BASE)
                        .header(HttpHeaders.AUTHORIZATION, contentManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDubbing_NonExistentId_ReturnsNotFound() throws Exception {
        Integer dubbingId = 999;
        UpdateDubbingDto dto = new UpdateDubbingDto("Updated Name");

        when(dubbingService.update(eq(dubbingId), any(UpdateDubbingDto.class)))
                .thenThrow(new NotFoundException(DubbingDto.class, dubbingId.toString()));

        mvc.perform(put(ApiPaths.FILM_DUBBING_API.BASE + ApiPaths.FILM_DUBBING_API.ID, dubbingId)
                        .header(HttpHeaders.AUTHORIZATION, contentManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }


    @Test
    void deleteDubbing_NonExistentId_ReturnsNotFound() throws Exception {
        Integer dubbingId = 999;

        doNothing().when(dubbingService).delete(dubbingId);

        mvc.perform(delete(ApiPaths.FILM_DUBBING_API.BASE + ApiPaths.FILM_DUBBING_API.ID, dubbingId)
                        .header(HttpHeaders.AUTHORIZATION, contentManagerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void viewerCannotUpdateOrDeleteDubbing_ReturnsForbidden() throws Exception {
        Integer dubbingId = 1;
        UpdateDubbingDto dto = new UpdateDubbingDto("Updated Name");

        mvc.perform(put(ApiPaths.FILM_DUBBING_API.BASE + ApiPaths.FILM_DUBBING_API.ID, dubbingId)
                        .header(HttpHeaders.AUTHORIZATION, viewerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        mvc.perform(delete(ApiPaths.FILM_DUBBING_API.BASE + ApiPaths.FILM_DUBBING_API.ID, dubbingId)
                        .header(HttpHeaders.AUTHORIZATION, viewerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void anonymousCannotUpdateOrDeleteDubbing_ReturnsUnauthorized() throws Exception {
        Integer dubbingId = 1;
        UpdateDubbingDto dto = new UpdateDubbingDto("Updated Name");

        mvc.perform(put(ApiPaths.FILM_DUBBING_API.BASE + ApiPaths.FILM_DUBBING_API.ID, dubbingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        mvc.perform(delete(ApiPaths.FILM_DUBBING_API.BASE + ApiPaths.FILM_DUBBING_API.ID, dubbingId))
                .andExpect(status().isUnauthorized());
    }
}