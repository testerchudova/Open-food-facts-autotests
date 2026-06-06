package qa.openfoodfacts.helpers;

import io.qameta.allure.restassured.AllureRestAssured;

public class CustomApiListener {

    private CustomApiListener() {
    }

    public static AllureRestAssured withCustomTemplates() {
        return new AllureRestAssured()
                .setRequestTemplate("request.ftl")
                .setResponseTemplate("response.ftl");
    }
}
