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
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateWatchRoomDto;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;
import ua.edu.ukma.springers.rezflix.security.CustomUserDetailsService;
import ua.edu.ukma.springers.rezflix.security.JWTService;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;
import ua.edu.ukma.springers.rezflix.services.WatchRoomService;
import ua.edu.ukma.springers.rezflix.utils.ApiPaths;
import ua.edu.ukma.springers.rezflix.utils.DefaultMessageResolver;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WatchRoomController.class)
@ActiveProfiles("test")
@Import({
        WebSecurityConfiguration.class,
        CustomUserDetailsService.class,
        JWTService.class,
        SecurityUtils.class,
        DefaultMessageResolver.class
})
class WatchRoomControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private WatchRoomService watchRoomService;
    @MockitoBean
    private UserRepository userRepository;

    private String viewerToken;

    @BeforeEach
    void setUp() {
        String viewerUsername = "viewer";
        UserEntity viewer = new UserEntity();
        viewer.setId(10);
        viewer.setUsername(viewerUsername);
        viewer.setPasswordHash(passwordEncoder.encode("password"));
        viewer.setType(UserType.VIEWER);

        when(userRepository.findByUsername(viewerUsername)).thenReturn(Optional.of(viewer));

        this.viewerToken = "Bearer " + jwtService.generateAccessToken(viewerUsername, UserRole.VIEWER.name());
    }

    @Test
    void createWatchRoom_Viewer_ReturnsUuid() throws Exception {
        UUID episodeId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();

        CreateWatchRoomDto dto = new CreateWatchRoomDto(episodeId);

        when(watchRoomService.createWatchRoom(any(CreateWatchRoomDto.class))).thenReturn(roomId);

        mvc.perform(post(ApiPaths.FILM_WATCH_API.BASE)
                        .header(HttpHeaders.AUTHORIZATION, viewerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(roomId.toString()));
    }

    @Test
    void createWatchRoom_Anonymous_ReturnsUnauthorized() throws Exception {
        CreateWatchRoomDto dto = new CreateWatchRoomDto(UUID.randomUUID());

        mvc.perform(post(ApiPaths.FILM_WATCH_API.BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}