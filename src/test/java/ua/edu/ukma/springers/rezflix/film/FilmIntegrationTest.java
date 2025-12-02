package ua.edu.ukma.springers.rezflix.film;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.ukma.springers.rezflix.BaseIntegrationTest;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;
import ua.edu.ukma.springers.rezflix.utils.GeneralRequests;
import ua.edu.ukma.springers.rezflix.utils.UserRequests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ua.edu.ukma.springers.rezflix.utils.RandomUtils.getRandomString;

class FilmIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private GeneralRequests generalRequests;

    @Autowired
    private UserRequests userRequests;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String FILM_API_PATH = "/api/film";

    @AfterEach
    void cleanUp() {
        filmRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Content Manager can perform full CRUD cycle on Films")
    void filmCrudCycle() {
        String superAdminToken = generalRequests.getSuperAdminAuthToken();
        String cmUsername = "content_manager_" + getRandomString(5);
        String cmPassword = "password";

        CreateUserDto createUserDto = new CreateUserDto(
                UserTypeDto.CONTENT_MANAGER,
                cmUsername,
                cmPassword
        );
        userRequests.createUser(createUserDto, superAdminToken);
        String cmToken = generalRequests.getAuthToken(cmUsername, cmPassword);
        UpsertFilmDto createFilmDto = new UpsertFilmDto();
        createFilmDto.setTitle("Inception");
        createFilmDto.setDescription("Dreams within dreams");
        Integer filmId = generalRequests.create(createFilmDto, FILM_API_PATH, cmToken, Integer.class);
        assertThat(filmId).isNotNull();
        FilmDto retrievedFilm = generalRequests.get(FILM_API_PATH + "/" + filmId, cmToken, FilmDto.class);
        assertEquals(createFilmDto.getTitle(), retrievedFilm.getTitle());
        assertEquals(createFilmDto.getDescription(), retrievedFilm.getDescription());
        assertEquals(0.0, retrievedFilm.getTotalRating());
        UpsertFilmDto updateFilmDto = new UpsertFilmDto();
        updateFilmDto.setTitle("Inception: Updated");
        updateFilmDto.setDescription("Updated description");
        generalRequests.update(updateFilmDto, FILM_API_PATH + "/" + filmId, cmToken);
        FilmDto updatedFilm = generalRequests.get(FILM_API_PATH + "/" + filmId, "", FilmDto.class); // "" - без токена
        assertEquals("Inception: Updated", updatedFilm.getTitle());
        generalRequests.delete(FILM_API_PATH + "/" + filmId, cmToken);
        generalRequests.getFail(FILM_API_PATH + "/" + filmId, cmToken, 404);
    }

    @Test
    @DisplayName("Viewer cannot create films")
    void viewerCannotCreateFilm() {
        RegisterUserDto registerDto = new RegisterUserDto("viewer_user", "pass");
        userRequests.registerUser(registerDto);
        String viewerToken = generalRequests.getAuthToken("viewer_user", "pass");
        UpsertFilmDto createFilmDto = new UpsertFilmDto();
        createFilmDto.setTitle("Illegal Film");
        createFilmDto.setDescription("Should fail");
        generalRequests.createFail(createFilmDto, FILM_API_PATH, viewerToken, 403);
    }
}