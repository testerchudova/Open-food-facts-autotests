package qa.openfoodfacts.api;

import qa.openfoodfacts.models.search.SearchResponseModel;
import qa.openfoodfacts.specs.SearchSpec;

import static io.restassured.RestAssured.given;

public class SearchApiClient extends ApiClient {

    public SearchResponseModel searchProducts(String searchTerm, int pageSize) {
        return withNetworkRetry(() ->
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
    }
}
