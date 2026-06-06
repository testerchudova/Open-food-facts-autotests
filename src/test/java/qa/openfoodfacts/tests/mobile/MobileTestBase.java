package qa.openfoodfacts.tests.mobile;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.remote.RemoteWebDriver;
import qa.openfoodfacts.config.MobileConfig;
import qa.openfoodfacts.drivers.OpenFoodFactsMobileDriverProvider;
import qa.openfoodfacts.helpers.Attach;

import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;

public class MobileTestBase {

    private static final MobileConfig CONFIG = ConfigFactory.create(MobileConfig.class, System.getProperties());

    @BeforeAll
    static void setUp() {
        Configuration.browser = OpenFoodFactsMobileDriverProvider.class.getName();
        Configuration.browserSize = null;
        Configuration.timeout = 15000;

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true));
    }

    @AfterEach
    void tearDown() {
        if (hasWebDriverStarted()) {
            Attach.screenshotAs("Mobile screenshot");
            Attach.pageSource();
            attachBrowserStackVideo();
            closeWebDriver();
        }
    }

    private void attachBrowserStackVideo() {
        if (!"browserstack".equalsIgnoreCase(CONFIG.deviceHost())) {
            return;
        }

        RemoteWebDriver driver = (RemoteWebDriver) getWebDriver();
        String sessionId = driver.getSessionId().toString();

        try {
            Attach.browserStackVideo(sessionId);
        } catch (RuntimeException e) {
            Attach.textAs("BrowserStack video", "Could not attach BrowserStack video: " + e.getMessage());
        }
    }
}
