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
                .auth().preemptive().basic(CONFIG.userName(), CONFIG.accessKey())
                .when()
                .get(SESSION_URL, sessionId)
                .then()
                .statusCode(200)
                .extract()
                .path("automation_session.video_url");
    }
}
