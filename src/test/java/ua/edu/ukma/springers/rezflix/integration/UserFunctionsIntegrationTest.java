package ua.edu.ukma.springers.rezflix.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.ukma.springers.rezflix.BaseIntegrationTest;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CommentDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateCommentDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateDubbingDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateWatchRoomDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CurrentUserInfoDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmCollectionListDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmRatingDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpdateCommentDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpsertFilmCollectionDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpsertFilmDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmDubbingEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEpisodeEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.FilmEpisodeStatus;
import ua.edu.ukma.springers.rezflix.repositories.*;
import ua.edu.ukma.springers.rezflix.utils.GeneralRequests;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.edu.ukma.springers.rezflix.utils.RandomUtils.getRandomString;


class UserFunctionsIntegrationTest extends BaseIntegrationTest {

    @Autowired private IntegrationTestHelper apiHelper;
    @Autowired private GeneralRequests requests;
    @Autowired private FilmEpisodeRepository episodeRepository;
    @Autowired private FilmDubbingRepository dubbingRepository;
    @Autowired private WatchRoomRepository watchRoomRepository;
    @Autowired private FilmRepository filmRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private FilmCollectionRepository collectionRepository;
    @Autowired private FilmCommentRepository commentRepository;
    @Autowired private FilmRatingRepository ratingRepository;
    @Autowired private FilmRecommendationsRepository recommendationRepository;
    @Autowired private FileInfoRepository fileInfoRepository;

    private String viewerToken;
    private Integer filmId;
    private String baseFilmPath;
    private String baseCollectionPath;
    private String baseCommentPath;
    private String baseWatchRoomPath;

    @BeforeEach
    void setup() {
        String cmToken = "Bearer " + apiHelper.createContentManagerAndGetToken();
        baseFilmPath = "/api/film";
        baseCollectionPath = "/api/film-collections";
        baseCommentPath = "/api/film/comment";
        baseWatchRoomPath = "/api/watch-room";

        filmId = requests.create(new UpsertFilmDto("Social Film " + getRandomString(5), "Desc"), baseFilmPath, cmToken, Integer.class);
        viewerToken = "Bearer " + apiHelper.createViewerAndGetToken();
    }

    @AfterEach
    void tearDown() {
        watchRoomRepository.deleteAll();
        commentRepository.deleteAll();
        recommendationRepository.deleteAll();
        ratingRepository.deleteAll();
        collectionRepository.deleteAll();
        episodeRepository.deleteAll();
        dubbingRepository.deleteAll();
        fileInfoRepository.deleteAll();
        filmRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void ratingAndRecommendationsFlow() {
        String ratingPath = baseFilmPath + "/" + filmId + "/rating";
        requests.update(new FilmRatingDto(5), ratingPath, viewerToken);

        FilmRatingDto rating = requests.get(ratingPath, viewerToken, FilmRatingDto.class);
        assertThat(rating.getRating()).isEqualTo(5);

        List<?> recs = requests.get(baseFilmPath + "/recommendations", viewerToken, List.class);
        assertThat(recs).isNotNull();

        requests.delete(ratingPath, viewerToken);
        requests.getFail(ratingPath, viewerToken, 404);
    }

    @Test
    void rating_EdgeCase_InvalidScoreTooLow() {
        requests.updateFail(new FilmRatingDto(0), baseFilmPath + "/" + filmId + "/rating", viewerToken, 400);
    }

    @Test
    void rating_EdgeCase_InvalidScoreTooHigh() {
        requests.updateFail(new FilmRatingDto(6), baseFilmPath + "/" + filmId + "/rating", viewerToken, 400);
    }

    @Test
    void rating_EdgeCase_NonExistentFilm() {
        requests.updateFail(new FilmRatingDto(5), baseFilmPath + "/999999/rating", viewerToken, 400);
    }

    @Test
    void commentsFlow() {
        CreateCommentDto createDto = new CreateCommentDto();
        createDto.setFilmId(filmId);
        createDto.setText("Awesome!");
        Integer commentId = requests.create(createDto, baseCommentPath, viewerToken, Integer.class);

        CommentDto comment = requests.get(baseCommentPath + "/" + commentId, viewerToken, CommentDto.class);
        assertThat(comment.getText()).isEqualTo("Awesome!");

        requests.update(new UpdateCommentDto("Edited text"), baseCommentPath + "/" + commentId, viewerToken);

        requests.delete(baseCommentPath + "/" + commentId, viewerToken);
        requests.getFail(baseCommentPath + "/" + commentId, viewerToken, 404);
    }

    @Test
    void comment_EdgeCase_EmptyText() {
        CreateCommentDto dto = new CreateCommentDto();
        dto.setFilmId(filmId);
        dto.setText("");
        requests.createFail(dto, baseCommentPath, viewerToken, 400);
    }

    @Test
    void comment_EdgeCase_TextTooLong() {
        CreateCommentDto dto = new CreateCommentDto();
        dto.setFilmId(filmId);
        dto.setText(getRandomString(2001));
        requests.createFail(dto, baseCommentPath, viewerToken, 400);
    }

    @Test
    void comment_EdgeCase_NonExistentFilm() {
        CreateCommentDto dto = new CreateCommentDto();
        dto.setFilmId(999999);
        dto.setText("Text");
        requests.createFail(dto, baseCommentPath, viewerToken, 400);
    }

    @Test
    void comment_EdgeCase_UpdateOtherUserComment() {
        CreateCommentDto dto = new CreateCommentDto();
        dto.setFilmId(filmId);
        dto.setText("My comment");
        Integer commentId = requests.create(dto, baseCommentPath, viewerToken, Integer.class);

        String otherToken = "Bearer " + apiHelper.createViewerAndGetToken();
        requests.updateFail(new UpdateCommentDto("Hacked"), baseCommentPath + "/" + commentId, otherToken, 403);
    }

    @Test
    void collectionsFlow() {
        UpsertFilmCollectionDto dto = new UpsertFilmCollectionDto();
        dto.setName("Favs");
        dto.setFilmIds(List.of(filmId));

        Integer colId = requests.create(dto, baseCollectionPath, viewerToken, Integer.class);
        FilmCollectionDto fetched = requests.get(baseCollectionPath + "/" + colId, viewerToken, FilmCollectionDto.class);
        assertThat(fetched.getFilms()).hasSize(1);

        CurrentUserInfoDto currentUser = requests.get("/api/user/current", viewerToken, CurrentUserInfoDto.class);
        FilmCollectionListDto list = requests.get(baseCollectionPath, viewerToken, Map.of("ownerId", currentUser.getInfo().getId()), FilmCollectionListDto.class);
        assertThat(list.getItems()).isNotEmpty();

        requests.delete(baseCollectionPath + "/" + colId, viewerToken);
        requests.getFail(baseCollectionPath + "/" + colId, viewerToken, 404);
    }

    @Test
    void collection_EdgeCase_EmptyName() {
        UpsertFilmCollectionDto dto = new UpsertFilmCollectionDto();
        dto.setName("");
        dto.setFilmIds(List.of(filmId));
        requests.createFail(dto, baseCollectionPath, viewerToken, 400);
    }

    @Test
    void collection_EdgeCase_NonExistentFilmId() {
        UpsertFilmCollectionDto dto = new UpsertFilmCollectionDto();
        dto.setName("List");
        dto.setFilmIds(List.of(999999));
        requests.createFail(dto, baseCollectionPath, viewerToken, 400);
    }

    @Test
    void collection_EdgeCase_UpdateOtherUserCollection() {
        UpsertFilmCollectionDto dto = new UpsertFilmCollectionDto();
        dto.setName("My List");
        dto.setFilmIds(List.of(filmId));
        Integer colId = requests.create(dto, baseCollectionPath, viewerToken, Integer.class);

        String otherToken = "Bearer " + apiHelper.createViewerAndGetToken();
        requests.updateFail(dto, baseCollectionPath + "/" + colId, otherToken, 403);
    }

    @Test
    void collection_EdgeCase_NonExistentCollection() {
        requests.getFail(baseCollectionPath + "/999999", viewerToken, 404);
    }

    @Test
    void watchRoomFlow() {
        FilmEpisodeEntity episode = createRenderedEpisode();

        CreateWatchRoomDto roomDto = new CreateWatchRoomDto();
        roomDto.setEpisodeId(episode.getId());
        roomDto.setPassword("1234");

        UUID roomId = requests.create(roomDto, baseWatchRoomPath, viewerToken, UUID.class);
        assertThat(roomId).isNotNull();
        assertThat(watchRoomRepository.findById(roomId)).isPresent();
    }

    @Test
    void watchRoom_EdgeCase_EpisodeNotRendered() {
        FilmEpisodeEntity episode = createRenderedEpisode();
        episode.setStatus(FilmEpisodeStatus.BEING_RENDERED);
        episodeRepository.save(episode);

        CreateWatchRoomDto roomDto = new CreateWatchRoomDto();
        roomDto.setEpisodeId(episode.getId());
        requests.createFail(roomDto, baseWatchRoomPath, viewerToken, 400);
    }

    @Test
    void watchRoom_EdgeCase_NonExistentEpisode() {
        CreateWatchRoomDto roomDto = new CreateWatchRoomDto();
        roomDto.setEpisodeId(UUID.randomUUID());
        requests.createFail(roomDto, baseWatchRoomPath, viewerToken, 400);
    }

    @Test
    void watchRoom_EdgeCase_NullEpisodeId() {
        CreateWatchRoomDto roomDto = new CreateWatchRoomDto();
        requests.createFail(roomDto, baseWatchRoomPath, viewerToken, 400);
    }

    private FilmEpisodeEntity createRenderedEpisode() {
        String cmToken = "Bearer " + apiHelper.createContentManagerAndGetToken();
        CreateDubbingDto dubDto = new CreateDubbingDto();
        dubDto.setFilmId(filmId);
        dubDto.setName("Dub " + getRandomString(5));
        Integer dubId = requests.create(dubDto, "/api/film/dubbing", cmToken, Integer.class);

        FilmDubbingEntity dubbingRef = dubbingRepository.getReferenceById(dubId);
        FilmEpisodeEntity episode = new FilmEpisodeEntity();
        episode.setFilmDubbing(dubbingRef);
        episode.setWatchOrder(1);
        episode.setTitle("Ep 1");
        episode.setStatus(FilmEpisodeStatus.RENDERED);
        return episodeRepository.save(episode);
    }
}