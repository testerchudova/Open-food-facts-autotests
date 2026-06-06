package qa.openfoodfacts.api;

import io.restassured.response.Response;
import qa.openfoodfacts.models.users.UserLoginRequestModel;
import qa.openfoodfacts.specs.ProductsSpec;

import static io.restassured.RestAssured.given;

public class AuthApiClient extends ApiClient {

    public Response loginOnStaging(UserLoginRequestModel request) {
        return withNetworkRetry(() ->
                given()
                        .spec(ProductsSpec.productFormRequestSpec())
                        .formParam("user_id", request.getUserId())
                        .formParam("password", request.getPassword())
                        .when()
                        .post("/cgi/session.pl")
                        .then()
                        .extract().response());
    }
}
