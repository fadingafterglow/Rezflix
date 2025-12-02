package ua.edu.ukma.springers.rezflix.api_tests;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.ukma.springers.rezflix.BaseIntegrationTest;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateDubbingDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.DubbingDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmListDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpdateDubbingDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpdateEpisodeDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpsertFilmDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEpisodeEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.FilmEpisodeStatus;
import ua.edu.ukma.springers.rezflix.repositories.FilmEpisodeRepository;
import ua.edu.ukma.springers.rezflix.utils.GeneralRequests;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static ua.edu.ukma.springers.rezflix.utils.RandomUtils.getRandomString;

class ContentApiTest extends BaseIntegrationTest {

    @Autowired private ApiTestHelper apiHelper;
    @Autowired private GeneralRequests requests;
    @Autowired private FilmEpisodeRepository episodeRepository;

    @Test
    void contentManagerShouldManageContentLifecycle() {
        String cmToken = "Bearer " + apiHelper.createContentManagerAndGetToken();

        UpsertFilmDto filmDto = new UpsertFilmDto("Content Film " + getRandomString(5), "Description");
        Integer filmId = requests.create(filmDto, "/api/film", cmToken, Integer.class);

        CreateDubbingDto dubbingDto = new CreateDubbingDto();
        dubbingDto.setFilmId(filmId);
        dubbingDto.setName("English Dub");
        Integer dubbingId = requests.create(dubbingDto, "/api/film/dubbing", cmToken, Integer.class);

        String episodeIdStr = given()
                .header("Authorization", cmToken)
                .contentType(ContentType.BINARY)
                .queryParam("title", "Pilot")
                .queryParam("watchOrder", 1)
                .body("fake video content".getBytes())
                .when()
                .post("/api/film/dubbing/" + dubbingId + "/episodes")
                .then()
                .statusCode(200)
                .body(notNullValue())
                .extract().asString().replace("\"", "");

        UUID episodeId = UUID.fromString(episodeIdStr);
        assertThat(episodeRepository.findById(episodeId)).isPresent();

        FilmEpisodeEntity episode = episodeRepository.findById(episodeId).get();
        assertThat(episode.getStatus()).isEqualTo(FilmEpisodeStatus.BEING_RENDERED);

        requests.update(new UpdateEpisodeDto(2, "Renamed"), "/api/film/episode/" + episodeId, cmToken);
        requests.delete("/api/film/episode/" + episodeId, cmToken);
        assertThat(episodeRepository.findById(episodeId)).isEmpty();
    }

    @Test
    void filmOperations() {
        String cmToken = "Bearer " + apiHelper.createContentManagerAndGetToken();
        Integer filmId = requests.create(new UpsertFilmDto("Film " + getRandomString(5), "Desc"), "/api/film", cmToken, Integer.class);

        FilmDto film = requests.get("/api/film/" + filmId, cmToken, FilmDto.class);
        assertThat(film.getTitle()).contains("Film");

        requests.delete("/api/film/" + filmId, cmToken);
        requests.getFail("/api/film/" + filmId, cmToken, 404);
    }

    @Test
    void dubbingOperations() {
        String cmToken = "Bearer " + apiHelper.createContentManagerAndGetToken();
        Integer filmId = requests.create(new UpsertFilmDto("Film " + getRandomString(5), "Desc"), "/api/film", cmToken, Integer.class);
        Integer dubId = requests.create(new CreateDubbingDto(filmId, "Dub 1"), "/api/film/dubbing", cmToken, Integer.class);

        DubbingDto dub = requests.get("/api/film/dubbing/" + dubId, cmToken, DubbingDto.class);
        assertThat(dub.getName()).isEqualTo("Dub 1");

        requests.update(new UpdateDubbingDto("Dub 2"), "/api/film/dubbing/" + dubId, cmToken);

        requests.delete("/api/film/dubbing/" + dubId, cmToken);
        requests.getFail("/api/film/dubbing/" + dubId, cmToken, 404);
    }

    @Test
    void viewerCanSearchFilms() {
        String cmToken = "Bearer " + apiHelper.createContentManagerAndGetToken();
        requests.create(new UpsertFilmDto("Matrix " + getRandomString(5), "Sci-Fi"), "/api/film", cmToken, Integer.class);

        FilmListDto list = requests.get("/api/film", "", FilmListDto.class);
        assertThat(list.getItems()).isNotEmpty();
    }
}