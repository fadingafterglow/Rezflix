package ua.edu.ukma.springers.rezflix.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateWatchRoomDto;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;
import ua.edu.ukma.springers.rezflix.services.WatchRoomService;
import ua.edu.ukma.springers.rezflix.utils.ApiPaths;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WatchRoomController.class)
class WatchRoomControllerTest extends BaseControllerTest {

    @MockitoBean
    private WatchRoomService watchRoomService;

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