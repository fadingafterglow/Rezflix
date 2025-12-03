package ua.edu.ukma.springers.rezflix.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionCriteriaDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionListDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpsertFilmCollectionDto;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;
import ua.edu.ukma.springers.rezflix.services.FilmCollectionService;
import ua.edu.ukma.springers.rezflix.utils.ApiPaths;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmCollectionController.class)
class FilmCollectionControllerTest extends BaseControllerTest {

    @MockitoBean
    private FilmCollectionService collectionService;

    private String viewerToken;
    private String moderatorToken;

    @BeforeEach
    void setUp() {
        String viewerUsername = "viewer";
        UserEntity viewer = new UserEntity();
        viewer.setId(10);
        viewer.setUsername(viewerUsername);
        viewer.setPasswordHash(passwordEncoder.encode("password"));
        viewer.setType(UserType.VIEWER);

        String modUsername = "moderator";
        UserEntity moderator = new UserEntity();
        moderator.setId(20);
        moderator.setUsername(modUsername);
        moderator.setPasswordHash(passwordEncoder.encode("password"));
        moderator.setType(UserType.MODERATOR);

        when(userRepository.findByUsername(viewerUsername)).thenReturn(Optional.of(viewer));
        when(userRepository.findByUsername(modUsername)).thenReturn(Optional.of(moderator));

        this.viewerToken = "Bearer " + jwtService.generateAccessToken(viewerUsername, UserRole.VIEWER.name());
        this.moderatorToken = "Bearer " + jwtService.generateAccessToken(modUsername, UserRole.MODERATOR.name());
    }

    @Test
    void getFilmCollection_Viewer_ReturnsCollection() throws Exception {
        FilmCollectionDto dto = new FilmCollectionDto();
        dto.setId(1);
        dto.setName("My Favorites");

        when(collectionService.getResponseById(1)).thenReturn(dto);

        mvc.perform(get(ApiPaths.FILM_COLLECTION_API.BASE + ApiPaths.FILM_COLLECTION_API.ID, 1)
                        .header(HttpHeaders.AUTHORIZATION, viewerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("My Favorites"));
    }

    @Test
    void getFilmCollectionsByCriteria_Moderator_ReturnsList() throws Exception {
        FilmCollectionListDto listDto = new FilmCollectionListDto();
        listDto.setItems(List.of());
        listDto.setTotal(0L);

        when(collectionService.getListResponseByCriteria(any(FilmCollectionCriteriaDto.class))).thenReturn(listDto);

        mvc.perform(get(ApiPaths.FILM_COLLECTION_API.BASE)
                        .header(HttpHeaders.AUTHORIZATION, moderatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void createFilmCollection_Viewer_ReturnsId() throws Exception {
        UpsertFilmCollectionDto dto = new UpsertFilmCollectionDto();
        dto.setName("Weekend Watch");


        when(collectionService.create(any(UpsertFilmCollectionDto.class))).thenReturn(100);

        mvc.perform(post(ApiPaths.FILM_COLLECTION_API.BASE)
                        .header(HttpHeaders.AUTHORIZATION, viewerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(100));
    }

    @Test
    void createFilmCollection_Anonymous_ReturnsUnauthorized() throws Exception {
        UpsertFilmCollectionDto dto = new UpsertFilmCollectionDto();
        dto.setName("Hidden Collection");

        mvc.perform(post(ApiPaths.FILM_COLLECTION_API.BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateFilmCollection_Viewer_ReturnsNoContent() throws Exception {
        Integer collectionId = 1;
        UpsertFilmCollectionDto dto = new UpsertFilmCollectionDto();
        dto.setName("Updated Name");

        when(collectionService.update(eq(collectionId), any(UpsertFilmCollectionDto.class))).thenReturn(true);

        mvc.perform(put(ApiPaths.FILM_COLLECTION_API.BASE + ApiPaths.FILM_COLLECTION_API.ID, collectionId)
                        .header(HttpHeaders.AUTHORIZATION, viewerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteFilmCollection_Moderator_ReturnsNoContent() throws Exception {
        Integer collectionId = 1;

        doNothing().when(collectionService).delete(collectionId);

        mvc.perform(delete(ApiPaths.FILM_COLLECTION_API.BASE + ApiPaths.FILM_COLLECTION_API.ID, collectionId)
                        .header(HttpHeaders.AUTHORIZATION, moderatorToken))
                .andExpect(status().isNoContent());
    }
}