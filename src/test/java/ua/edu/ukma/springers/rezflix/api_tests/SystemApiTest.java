package ua.edu.ukma.springers.rezflix.api_tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.ukma.springers.rezflix.BaseIntegrationTest;
import ua.edu.ukma.springers.rezflix.utils.GeneralRequests;

class SystemApiTest extends BaseIntegrationTest {

    @Autowired private ApiTestHelper apiHelper;
    @Autowired private GeneralRequests requests;

    @Test
    void superAdminCanClearCache() {
        String adminToken = apiHelper.getSuperAdminToken();
        requests.delete("/api/cache/film", adminToken);
    }

    @Test
    void regularUserCannotClearCache() {
        String userToken = "Bearer " + apiHelper.createViewerAndGetToken();
        requests.deleteFail("/api/cache/film", userToken, 403);
    }
}