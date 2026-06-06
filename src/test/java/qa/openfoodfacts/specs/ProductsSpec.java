package qa.openfoodfacts.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class ProductsSpec {

    private ProductsSpec() {
    }

    public static RequestSpecification productRequestSpec() {
        return new RequestSpecBuilder()
                .addRequestSpecification(BaseSpec.openFoodFactsRequestSpec())
                .build();
    }

    public static RequestSpecification productFormRequestSpec() {
        return new RequestSpecBuilder()
                .addRequestSpecification(BaseSpec.openFoodFactsFormRequestSpec())
                .build();
    }

    public static ResponseSpecification successResponseSpec() {
        return BaseSpec.responseSpec(200);
    }

    public static ResponseSpecification responseSpec(int statusCode) {
        return BaseSpec.responseSpec(statusCode);
    }
}
