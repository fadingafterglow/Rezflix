package ua.edu.ukma.springers.rezflix.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;
import ua.edu.ukma.springers.rezflix.services.FilmCommentService;
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

@WebMvcTest(FilmCommentController.class)
class FilmCommentControllerTest extends BaseControllerTest {

    @MockitoBean
    private FilmCommentService commentService;

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
    void getComment_PublicEndpoint_ReturnsComment() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setId(1);
        dto.setText("Great movie!");

        when(commentService.getResponseById(1)).thenReturn(dto);

        mvc.perform(get(ApiPaths.FILM_COMMENT_API.BASE + ApiPaths.FILM_COMMENT_API.ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Great movie!"));
    }

    @Test
    void getCommentsByCriteria_PublicEndpoint_ReturnsList() throws Exception {
        CommentListDto listDto = new CommentListDto();
        listDto.setItems(List.of());
        listDto.setTotal(0L);

        when(commentService.getListResponseByCriteria(any(CommentCriteriaDto.class))).thenReturn(listDto);

        mvc.perform(get(ApiPaths.FILM_COMMENT_API.BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void createComment_Viewer_ReturnsId() throws Exception {
        CreateCommentDto dto = new CreateCommentDto();
        dto.setFilmId(1);
        dto.setText("I liked it");

        when(commentService.create(any(CreateCommentDto.class))).thenReturn(101);

        mvc.perform(post(ApiPaths.FILM_COMMENT_API.BASE)
                        .header(HttpHeaders.AUTHORIZATION, viewerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(101));
    }

    @Test
    void createComment_Anonymous_ReturnsUnauthorized() throws Exception {
        CreateCommentDto dto = new CreateCommentDto();
        dto.setFilmId(1);
        dto.setText("Anon comment");

        mvc.perform(post(ApiPaths.FILM_COMMENT_API.BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateComment_Viewer_ReturnsNoContent() throws Exception {
        Integer commentId = 1;
        UpdateCommentDto dto = new UpdateCommentDto("Updated text");

        when(commentService.update(eq(commentId), any(UpdateCommentDto.class))).thenReturn(true);

        mvc.perform(put(ApiPaths.FILM_COMMENT_API.BASE + ApiPaths.FILM_COMMENT_API.ID, commentId)
                        .header(HttpHeaders.AUTHORIZATION, viewerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteComment_Viewer_ReturnsNoContent() throws Exception {
        Integer commentId = 1;
        doNothing().when(commentService).delete(commentId);

        mvc.perform(delete(ApiPaths.FILM_COMMENT_API.BASE + ApiPaths.FILM_COMMENT_API.ID, commentId)
                        .header(HttpHeaders.AUTHORIZATION, viewerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteComment_Moderator_ReturnsNoContent() throws Exception {
        Integer commentId = 1;
        doNothing().when(commentService).delete(commentId);

        mvc.perform(delete(ApiPaths.FILM_COMMENT_API.BASE + ApiPaths.FILM_COMMENT_API.ID, commentId)
                        .header(HttpHeaders.AUTHORIZATION, moderatorToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteComment_Anonymous_ReturnsUnauthorized() throws Exception {
        Integer commentId = 1;
        mvc.perform(delete(ApiPaths.FILM_COMMENT_API.BASE + ApiPaths.FILM_COMMENT_API.ID, commentId))
                .andExpect(status().isUnauthorized());
    }
}