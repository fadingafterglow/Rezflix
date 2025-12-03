package ua.edu.ukma.springers.rezflix.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.ukma.springers.rezflix.BaseIntegrationTest;
import ua.edu.ukma.springers.rezflix.utils.GeneralRequests;

class CacheApiIntegrationTest extends BaseIntegrationTest {

    @Autowired private IntegrationTestHelper apiHelper;
    @Autowired private GeneralRequests requests;

    private String adminToken;
    private String baseCachePath;

    @BeforeEach
    void setUp() {
        adminToken = apiHelper.getSuperAdminToken();
        baseCachePath = "/api/cache";
    }

    @Test
    void superAdminCanClearCache() {
        requests.delete(baseCachePath + "/film", adminToken);
    }

    @Test
    void clearCache_EdgeCase_RegularUserForbidden() {
        String userToken = "Bearer " + apiHelper.createViewerAndGetToken();
        requests.deleteFail(baseCachePath + "/film", userToken, 403);
    }

    @Test
    void clearCache_EdgeCase_ContentManagerForbidden() {
        String cmToken = "Bearer " + apiHelper.createContentManagerAndGetToken();
        requests.deleteFail(baseCachePath + "/film", cmToken, 403);
    }

    @Test
    void clearCache_EdgeCase_AnonymousUnauthorized() {
        requests.deleteFail(baseCachePath + "/film", "", 401);
    }

    @Test
    void clearCache_EdgeCase_NonExistentCacheName() {
        requests.deleteFail(baseCachePath + "/non_existent_cache", adminToken, 404);
    }

    @Test
    void clearCache_EdgeCase_EmptyCacheName() {
        requests.deleteFail(baseCachePath + "/ ", adminToken, 404);
    }

    @Test
    void clearCache_EdgeCase_SpecialCharactersInName() {
        requests.deleteFail(baseCachePath + "/$#@!", adminToken, 404);
    }

    @Test
    void clearCache_EdgeCase_MethodNotAllowed() {
        requests.getFail(baseCachePath + "/film", adminToken, 405);
    }
}