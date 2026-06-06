package qa.openfoodfacts.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class SearchSpec {

    private SearchSpec() {
    }

    public static RequestSpecification searchRequestSpec() {
        return new RequestSpecBuilder()
                .addRequestSpecification(BaseSpec.openFoodFactsRequestSpec())
                .build();
    }

    public static ResponseSpecification successResponseSpec() {
        return BaseSpec.responseSpec(200);
    }
}
