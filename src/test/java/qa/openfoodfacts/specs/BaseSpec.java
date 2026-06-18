package qa.openfoodfacts.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.aeonbits.owner.ConfigFactory;
import qa.openfoodfacts.config.ApiConfig;
import qa.openfoodfacts.helpers.CustomApiListener;

public class BaseSpec {

    private static final ApiConfig CONFIG = ConfigFactory.create(ApiConfig.class, System.getProperties());

    private BaseSpec() {
    }

    public static RequestSpecification openFoodFactsRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(CONFIG.apiBaseUrl())
                .setConfig(restAssuredTimeoutConfig())
                .addHeader("User-Agent", CONFIG.userAgent())
                .addHeader("Accept-Encoding", "identity")
                .setAccept(ContentType.JSON)
                .addFilter(CustomApiListener.withCustomTemplates())
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    public static RequestSpecification openFoodFactsFormRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(CONFIG.stagingBaseUrl())
                .setConfig(restAssuredTimeoutConfig())
                .addHeader("User-Agent", CONFIG.userAgent())
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.URLENC)
                .addFilter(CustomApiListener.withCustomTemplates())
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    private static RestAssuredConfig restAssuredTimeoutConfig() {
        return RestAssuredConfig.config()
                .sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation())
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 20000)
                        .setParam("http.socket.timeout", 20000)
                        .setParam("http.connection-manager.timeout", 20000));
    }

    public static ResponseSpecification responseSpec(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .build();
    }
}
