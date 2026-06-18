package qa.openfoodfacts.api;

import qa.openfoodfacts.models.search.SearchResponseModel;
import qa.openfoodfacts.specs.SearchSpec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.restassured.RestAssured.given;

public class SearchApiClient extends ApiClient {

    private static final Map<String, SearchResponseModel> SEARCH_CACHE = new ConcurrentHashMap<>();

    public SearchResponseModel searchProducts(String searchTerm, int pageSize) {
        String cacheKey = searchTerm + ":" + pageSize;
        if (SEARCH_CACHE.containsKey(cacheKey)) {
            return SEARCH_CACHE.get(cacheKey);
        }

        SearchResponseModel response = withNetworkRetry(() ->
                given()
                        .spec(SearchSpec.searchRequestSpec())
                        .queryParam("search_terms", searchTerm)
                        .queryParam("fields", "code,product_name,brands,categories,image_front_url")
                        .queryParam("page_size", pageSize)
                        .when()
                        .get("/api/v2/search")
                        .then()
                        .spec(SearchSpec.successResponseSpec())
                        .extract().as(SearchResponseModel.class));
        SEARCH_CACHE.put(cacheKey, response);
        return response;
    }

    public SearchResponseModel searchProductsByPost(String searchTerm, int pageSize) {
        return withNetworkRetry(() ->
                given()
                        .spec(SearchSpec.searchRequestSpec())
                        .formParam("search_terms", searchTerm)
                        .formParam("search_simple", 1)
                        .formParam("action", "process")
                        .formParam("json", 1)
                        .formParam("fields", "code,product_name,brands,categories,image_front_url")
                        .formParam("page_size", pageSize)
                        .when()
                        .post("/cgi/search.pl")
                        .then()
                        .spec(SearchSpec.successResponseSpec())
                        .extract().as(SearchResponseModel.class));
    }
}
