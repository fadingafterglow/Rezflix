package ua.edu.ukma.springers.rezflix.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.edu.ukma.springers.rezflix.configuration.WebSecurityConfiguration;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;
import ua.edu.ukma.springers.rezflix.security.CustomUserDetailsService;
import ua.edu.ukma.springers.rezflix.security.JWTService;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;
import ua.edu.ukma.springers.rezflix.services.FilmEpisodeService;
import ua.edu.ukma.springers.rezflix.utils.ApiPaths;
import ua.edu.ukma.springers.rezflix.utils.DefaultMessageResolver;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmEpisodeController.class)
@ActiveProfiles("test")
@Import({
        WebSecurityConfiguration.class,
        CustomUserDetailsService.class,
        JWTService.class,
        SecurityUtils.class,
        DefaultMessageResolver.class
})
class FilmEpisodeControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private FilmEpisodeService episodeService;
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
    void getEpisode_PublicEndpoint_ReturnsEpisode() throws Exception {
        UUID episodeId = UUID.randomUUID();
        EpisodeDto dto = new EpisodeDto();
        dto.setId(episodeId);
        dto.setTitle("Pilot Episode");

        when(episodeService.getResponseById(episodeId)).thenReturn(dto);

        mvc.perform(get(ApiPaths.FILM_EPISPODES_API.BASE + ApiPaths.FILM_EPISPODES_API.ID, episodeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(episodeId.toString()))
                .andExpect(jsonPath("$.title").value("Pilot Episode"));
    }

    @Test
    void getEpisodesByCriteria_PublicEndpoint_ReturnsList() throws Exception {
        Integer dubbingId = 100;
        EpisodeListDto listDto = new EpisodeListDto();
        listDto.setItems(List.of());

        when(episodeService.getListResponseByCriteria(eq(dubbingId), any(EpisodeCriteriaDto.class))).thenReturn(listDto);

        mvc.perform(get(ApiPaths.FILM_EPISPODES_API.EPISODS, dubbingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void createEpisode_Viewer_ReturnsForbidden() throws Exception {
        Integer dubbingId = 100;
        MockMultipartFile filePart = new MockMultipartFile("file", "test".getBytes());
        MockMultipartFile metadataPart = new MockMultipartFile("metadata", "{}".getBytes());

        mvc.perform(multipart(ApiPaths.FILM_EPISPODES_API.EPISODS, dubbingId)
                        .file(filePart)
                        .file(metadataPart)
                        .header(HttpHeaders.AUTHORIZATION, viewerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateEpisodeMetadata_ContentManager_ReturnsNoContent() throws Exception {
        UUID episodeId = UUID.randomUUID();
        UpdateEpisodeDto dto = new UpdateEpisodeDto(2, "Updated Title");

        when(episodeService.update(eq(episodeId), any(UpdateEpisodeDto.class))).thenReturn(true);

        mvc.perform(put(ApiPaths.FILM_EPISPODES_API.BASE + ApiPaths.FILM_EPISPODES_API.ID, episodeId)
                        .header(HttpHeaders.AUTHORIZATION, contentManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEpisode_ContentManager_ReturnsNoContent() throws Exception {
        UUID episodeId = UUID.randomUUID();

        doNothing().when(episodeService).delete(episodeId);

        mvc.perform(delete(ApiPaths.FILM_EPISPODES_API.BASE + ApiPaths.FILM_EPISPODES_API.ID, episodeId)
                        .header(HttpHeaders.AUTHORIZATION, contentManagerToken))
                .andExpect(status().isNoContent());
    }
}