package ua.edu.ukma.springers.rezflix.integration;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.ukma.springers.rezflix.BaseIntegrationTest;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateDubbingDto;
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
import org.junit.jupiter.api.BeforeEach;

class ContentApiIntegrationTest extends BaseIntegrationTest {

    @Autowired private IntegrationTestHelper apiHelper;
    @Autowired private GeneralRequests requests;
    @Autowired private FilmEpisodeRepository episodeRepository;

    private String cmToken;
    private String viewerToken;
    private String baseFilmPath;
    private String baseDubbingPath;

    @BeforeEach
    void setUp() {
        cmToken = "Bearer " + apiHelper.createContentManagerAndGetToken();
        viewerToken = "Bearer " + apiHelper.createViewerAndGetToken();
        baseFilmPath = "/api/film";
        baseDubbingPath = "/api/film/dubbing";
    }

    @Test
    void contentManagerShouldManageContentLifecycle() {
        UpsertFilmDto filmDto = new UpsertFilmDto("Content Film " + getRandomString(5), "Description");
        Integer filmId = requests.create(filmDto, baseFilmPath, cmToken, Integer.class);

        CreateDubbingDto dubbingDto = new CreateDubbingDto();
        dubbingDto.setFilmId(filmId);
        dubbingDto.setName("English Dub");
        Integer dubbingId = requests.create(dubbingDto, baseDubbingPath, cmToken, Integer.class);

        String episodeIdStr = given()
                .header("Authorization", cmToken)
                .contentType(ContentType.BINARY)
                .queryParam("title", "Pilot")
                .queryParam("watchOrder", 1)
                .body("fake video content".getBytes())
                .when()
                .post(baseDubbingPath + "/" + dubbingId + "/episodes")
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
    void createFilm_EdgeCase_EmptyTitle() {
        UpsertFilmDto filmDto = new UpsertFilmDto("", "Desc");
        requests.createFail(filmDto, baseFilmPath, cmToken, 400);
    }

    @Test
    void createFilm_EdgeCase_DuplicateTitle() {
        String title = "Unique " + getRandomString(5);
        requests.create(new UpsertFilmDto(title, "Desc"), baseFilmPath, cmToken, Integer.class);
        requests.createFail(new UpsertFilmDto(title, "Other Desc"), baseFilmPath, cmToken, 400);
    }

    @Test
    void createFilm_EdgeCase_TitleTooLong() {
        String longTitle = getRandomString(251);
        UpsertFilmDto filmDto = new UpsertFilmDto(longTitle, "Desc");
        requests.createFail(filmDto, baseFilmPath, cmToken, 400);
    }

    @Test
    void createDubbing_EdgeCase_NonExistentFilm() {
        CreateDubbingDto dto = new CreateDubbingDto(999999, "Dub");
        requests.createFail(dto, baseDubbingPath, cmToken, 400);
    }

    @Test
    void createDubbing_EdgeCase_DuplicateNameInFilm() {
        Integer filmId = requests.create(new UpsertFilmDto("F " + getRandomString(5), "D"), baseFilmPath, cmToken, Integer.class);
        requests.create(new CreateDubbingDto(filmId, "Dub"), baseDubbingPath, cmToken, Integer.class);
        requests.createFail(new CreateDubbingDto(filmId, "Dub"), baseDubbingPath, cmToken, 400);
    }

    @Test
    void createEpisode_EdgeCase_NegativeWatchOrder() {
        Integer filmId = requests.create(new UpsertFilmDto("F " + getRandomString(5), "D"), baseFilmPath, cmToken, Integer.class);
        Integer dubId = requests.create(new CreateDubbingDto(filmId, "Dub"), baseDubbingPath, cmToken, Integer.class);

        given()
                .header("Authorization", cmToken)
                .contentType(ContentType.BINARY)
                .queryParam("watchOrder", -1)
                .body(new byte[]{1})
                .when()
                .post(baseDubbingPath + "/" + dubId + "/episodes")
                .then()
                .statusCode(400);
    }

    @Test
    void updateFilm_EdgeCase_ViewerForbidden() {
        Integer filmId = requests.create(new UpsertFilmDto("F " + getRandomString(5), "D"), baseFilmPath, cmToken, Integer.class);
        requests.updateFail(new UpsertFilmDto("New", "New"), baseFilmPath + "/" + filmId, viewerToken, 403);
    }

    @Test
    void getFilm_EdgeCase_NotFound() {
        requests.getFail(baseFilmPath + "/999999", viewerToken, 404);
    }
}