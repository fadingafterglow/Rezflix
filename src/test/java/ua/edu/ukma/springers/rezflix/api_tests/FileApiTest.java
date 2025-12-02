package ua.edu.ukma.springers.rezflix.api_tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.ukma.springers.rezflix.BaseIntegrationTest;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CurrentUserInfoDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.EntityFilesInfoDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FileTypeDto;
import ua.edu.ukma.springers.rezflix.utils.GeneralRequests;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.BeforeEach;

class FileApiTest extends BaseIntegrationTest {

    @Autowired private ApiTestHelper apiHelper;
    @Autowired private GeneralRequests requests;

    private String token;
    private Integer userId;
    private String baseFilePath;

    @BeforeEach
    void setUp() {
        token = "Bearer " + apiHelper.createViewerAndGetToken();
        CurrentUserInfoDto user = requests.get("/api/user/current", token, CurrentUserInfoDto.class);
        userId = user.getInfo().getId();
        baseFilePath = "/api/file";
    }

    @Test
    void shouldUploadAndDownloadUserAvatar() {
        String fileIdStr = given()
                .header("Authorization", token)
                .contentType("multipart/form-data")
                .multiPart("file", "avatar.png", "fake image content".getBytes(), "image/png")
                .multiPart("fileType", FileTypeDto.USER_AVATAR.toString())
                .multiPart("boundEntityId", userId.toString())
                .when()
                .post(baseFilePath)
                .then()
                .statusCode(200)
                .body(notNullValue())
                .extract().asString().replace("\"", "");

        UUID fileId = UUID.fromString(fileIdStr);

        List<EntityFilesInfoDto> info = requests.get(baseFilePath, "", Map.of("fileType", "USER_AVATAR", "entitiesIds", userId), List.class);
        assertThat(info).isNotNull();

        byte[] downloaded = given()
                .header("Authorization", token)
                .when()
                .get(baseFilePath + "/" + fileId)
                .then()
                .statusCode(200)
                .extract().asByteArray();

        assertThat(downloaded).isEqualTo("fake image content".getBytes());

        requests.delete(baseFilePath + "/" + fileId, token);
        requests.getFail(baseFilePath + "/" + fileId, token, 404);
    }

    @Test
    void upload_EdgeCase_EmptyFile() {
        given()
                .header("Authorization", token)
                .contentType("multipart/form-data")
                .multiPart("file", "empty.png", new byte[0], "image/png")
                .multiPart("fileType", FileTypeDto.USER_AVATAR.toString())
                .multiPart("boundEntityId", userId.toString())
                .when()
                .post(baseFilePath)
                .then()
                .statusCode(400);
    }

    @Test
    void upload_EdgeCase_InvalidContentType() {
        given()
                .header("Authorization", token)
                .contentType("multipart/form-data")
                .multiPart("file", "script.sh", "echo hello".getBytes(), "text/plain")
                .multiPart("fileType", FileTypeDto.USER_AVATAR.toString())
                .multiPart("boundEntityId", userId.toString())
                .when()
                .post(baseFilePath)
                .then()
                .statusCode(400);
    }

    @Test
    void upload_EdgeCase_NonExistentEntity() {
        given()
                .header("Authorization", token)
                .contentType("multipart/form-data")
                .multiPart("file", "img.png", "data".getBytes(), "image/png")
                .multiPart("fileType", FileTypeDto.USER_AVATAR.toString())
                .multiPart("boundEntityId", "999999")
                .when()
                .post(baseFilePath)
                .then()
                .statusCode(400);
    }

    @Test
    void delete_EdgeCase_NonExistentFile() {
        requests.deleteFail(baseFilePath + "/" + UUID.randomUUID(), token, 404);
    }

    @Test
    void delete_EdgeCase_NotOwnerForbidden() {
        String otherUserToken = "Bearer " + apiHelper.createViewerAndGetToken();

        String fileIdStr = given()
                .header("Authorization", token)
                .contentType("multipart/form-data")
                .multiPart("file", "avatar.png", "content".getBytes(), "image/png")
                .multiPart("fileType", FileTypeDto.USER_AVATAR.toString())
                .multiPart("boundEntityId", userId.toString())
                .when()
                .post(baseFilePath)
                .then()
                .extract().asString().replace("\"", "");

        requests.deleteFail(baseFilePath + "/" + fileIdStr, otherUserToken, 403);
    }

    @Test
    void upload_EdgeCase_MissingFileType() {
        given()
                .header("Authorization", token)
                .contentType("multipart/form-data")
                .multiPart("file", "img.png", "data".getBytes(), "image/png")
                .multiPart("boundEntityId", userId.toString())
                .when()
                .post(baseFilePath)
                .then()
                .statusCode(400);
    }

    @Test
    void getFile_EdgeCase_InvalidUUIDFormat() {
        requests.getFail(baseFilePath + "/invalid-uuid-string", token, 400);
    }
}