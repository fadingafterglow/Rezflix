package ua.edu.ukma.springers.rezflix.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.BaseCriteriaDto;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@Component
@RequiredArgsConstructor
public class GeneralRequests {

    private final ObjectMapper mapper;

    public String getAuthToken(String login, String password) {
        // TODO: implement token retrieval
        return "token";
    }

    public void getAuthTokenFail(String login, String password) {
        // TODO: implement token retrieval
    }

    public String getSuperAdminAuthToken() {
        // TODO: implement token retrieval
        return "super_admin_token";
    }

    public <View, Id> Id create(View dto, String apiPath, String authToken, Class<Id> idClass) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .body(dto)
                .expect()
                .statusCode(HttpServletResponse.SC_OK)
                .body(notNullValue())
                .when()
                .post(apiPath)
                .as(idClass);
    }

    public <View> void createFail(View dto, String apiPath, String authToken, int errorCode) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .body(dto)
                .expect()
                .statusCode(errorCode)
                .body(notNullValue())
                .when()
                .post(apiPath);
    }

    public <View> void update(View dto, String apiPath, String authToken) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .body(dto)
                .expect()
                .statusCode(HttpServletResponse.SC_NO_CONTENT)
                .body(notNullValue())
                .when()
                .put(apiPath);
    }

    public <View> void updateFail(View dto, String apiPath, String authToken, int errorCode) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .body(dto)
                .expect()
                .statusCode(errorCode)
                .body(notNullValue())
                .when()
                .put(apiPath);
    }

    public void delete(String apiPath, String authToken) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .expect()
                .statusCode(HttpServletResponse.SC_NO_CONTENT)
                .body(notNullValue())
                .when()
                .delete(apiPath);
    }

    public void deleteFail(String apiPath, String authToken, int errorCode) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .expect()
                .statusCode(errorCode)
                .body(notNullValue())
                .when()
                .delete(apiPath);
    }

    public <Response> Response get(String apiPath, String authToken, Class<Response> respClass) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .expect()
                .statusCode(HttpServletResponse.SC_OK)
                .body(notNullValue())
                .when()
                .get(apiPath)
                .as(respClass);
    }

    public <Response> Response get(String apiPath, String authToken, Map<String, ?> params, Class<Response> respClass) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .queryParams(params)
                .expect()
                .statusCode(HttpServletResponse.SC_OK)
                .body(notNullValue())
                .when()
                .get(apiPath)
                .as(respClass);
    }

    public void getFail(String apiPath, String authToken, int errorCode) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .expect()
                .statusCode(errorCode)
                .body(notNullValue())
                .when()
                .get(apiPath);
    }

    public void getFail(String apiPath, String authToken, Map<String, ?> params, int errorCode) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .queryParams(params)
                .expect()
                .statusCode(errorCode)
                .body(notNullValue())
                .when()
                .get(apiPath);
    }

    @SuppressWarnings("unchecked")
    public <Response, Criteria extends BaseCriteriaDto> Response getByCriteria(String apiPath, Criteria criteria, String authToken, Class<Response> respClass) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .queryParams(mapper.convertValue(criteria, Map.class))
                .expect()
                .statusCode(HttpServletResponse.SC_OK)
                .body(notNullValue())
                .when()
                .get(apiPath)
                .as(respClass);
    }

    @SuppressWarnings("unchecked")
    public <Criteria extends BaseCriteriaDto> void getByCriteriaFail(String apiPath, Criteria criteria, String authToken, int errorCode) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .queryParams(mapper.convertValue(criteria, Map.class))
                .expect()
                .statusCode(errorCode)
                .body(notNullValue())
                .when()
                .get(apiPath);
    }

}


