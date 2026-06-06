package qa.openfoodfacts.helpers;

import org.aeonbits.owner.ConfigFactory;
import qa.openfoodfacts.config.MobileConfig;

import static io.restassured.RestAssured.given;

public class BrowserStackHelper {

    private static final MobileConfig CONFIG = ConfigFactory.create(MobileConfig.class, System.getProperties());
    private static final String SESSION_URL = "https://api.browserstack.com/app-automate/sessions/{sessionId}.json";

    private BrowserStackHelper() {
    }

    public static String videoUrl(String sessionId) {
        return given()
                .auth().preemptive().basic(browserstackUser(), browserstackKey())
                .when()
                .get(SESSION_URL, sessionId)
                .then()
                .statusCode(200)
                .extract()
                .path("automation_session.video_url");
    }

    private static String browserstackUser() {
        return requiredBrowserStackValue("BROWSERSTACK_USER", CONFIG.userName());
    }

    private static String browserstackKey() {
        return requiredBrowserStackValue("BROWSERSTACK_KEY", CONFIG.accessKey());
    }

    private static String requiredBrowserStackValue(String envName, String propertyValue) {
        String value = firstNonBlank(System.getenv(envName), propertyValue);

        if (value.isBlank()) {
            throw new IllegalStateException(envName + " is required for BrowserStack mobile tests");
        }

        return value;
    }

    private static String firstNonBlank(String firstValue, String secondValue) {
        if (firstValue != null && !firstValue.isBlank()) {
            return firstValue.trim();
        }

        if (secondValue != null && !secondValue.isBlank()) {
            return secondValue.trim();
        }

        return "";
    }
}
