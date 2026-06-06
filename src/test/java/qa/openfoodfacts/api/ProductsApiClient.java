package qa.openfoodfacts.api;

import io.restassured.response.Response;
import qa.openfoodfacts.models.products.ProductResponseModel;
import qa.openfoodfacts.models.products.ProductUpdateRequestModel;
import qa.openfoodfacts.specs.ProductsSpec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.restassured.RestAssured.given;

public class ProductsApiClient extends ApiClient {

    private static final Map<String, ProductResponseModel> PRODUCT_CACHE = new ConcurrentHashMap<>();

    public ProductResponseModel getProductByBarcode(String barcode) {
        return getProductByBarcode(barcode, 200);
    }

    public ProductResponseModel getProductByBarcode(String barcode, int expectedStatusCode) {
        String cacheKey = barcode + ":" + expectedStatusCode;
        if (PRODUCT_CACHE.containsKey(cacheKey)) {
            return PRODUCT_CACHE.get(cacheKey);
        }

        ProductResponseModel response = withNetworkRetry(() ->
                given()
                        .spec(ProductsSpec.productRequestSpec())
                        .queryParam("fields", "code,product_name,brands,categories,quantity,nutriscore_grade,nutriments")
                        .when()
                        .get("/api/v0/product/{barcode}.json", barcode)
                        .then()
                        .spec(ProductsSpec.responseSpec(expectedStatusCode))
                        .extract().as(ProductResponseModel.class));
        PRODUCT_CACHE.put(cacheKey, response);
        return response;
    }

    public Response getRawProductByBarcode(String barcode) {
        return withNetworkRetry(() ->
                given()
                        .spec(ProductsSpec.productRequestSpec())
                        .queryParam("fields", "code,product_name,brands,categories,quantity,nutriscore_grade,nutriments")
                        .when()
                        .get("/api/v0/product/{barcode}.json", barcode)
                        .then()
                        .extract().response());
    }

    public Response createOrUpdateProductOnStaging(ProductUpdateRequestModel product) {
        return withNetworkRetry(() ->
                given()
                        .spec(ProductsSpec.productFormRequestSpec())
                        .formParam("user_id", credentialsConfig.userName())
                        .formParam("password", credentialsConfig.password())
                        .formParam("code", product.getCode())
                        .formParam("product_name", product.getProductName())
                        .formParam("brands", product.getBrands())
                        .formParam("categories", product.getCategories())
                        .formParam("quantity", product.getQuantity())
                        .formParam("comment", product.getComment())
                        .when()
                        .post("/cgi/product_jqm2.pl")
                        .then()
                        .extract().response());
    }
}
