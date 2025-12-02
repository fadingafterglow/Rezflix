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

class FileApiTest extends BaseIntegrationTest {

    @Autowired private ApiTestHelper apiHelper;
    @Autowired private GeneralRequests requests;

    @Test
    void shouldUploadAndDownloadUserAvatar() {
        String token = "Bearer " + apiHelper.createViewerAndGetToken();

        CurrentUserInfoDto user = requests.get("/api/user/current", token, CurrentUserInfoDto.class);
        Integer userId = user.getInfo().getId();

        String fileIdStr = given()
                .header("Authorization", token)
                .contentType("multipart/form-data")
                .multiPart("file", "avatar.png", "fake image content".getBytes(), "image/png")
                .multiPart("fileType", FileTypeDto.USER_AVATAR.toString())
                .multiPart("boundEntityId", userId.toString())
                .when()
                .post("/api/file")
                .then()
                .statusCode(200)
                .body(notNullValue())
                .extract().asString().replace("\"", "");

        UUID fileId = UUID.fromString(fileIdStr);

        List<EntityFilesInfoDto> info = requests.get("/api/file", "", Map.of("fileType", "USER_AVATAR", "entitiesIds", userId), List.class);
        assertThat(info).isNotNull();

        byte[] downloaded = given()
                .header("Authorization", token)
                .when()
                .get("/api/file/" + fileId)
                .then()
                .statusCode(200)
                .extract().asByteArray();

        assertThat(downloaded).isEqualTo("fake image content".getBytes());

        requests.delete("/api/file/" + fileId, token);
        requests.getFail("/api/file/" + fileId, token, 404);
    }
}