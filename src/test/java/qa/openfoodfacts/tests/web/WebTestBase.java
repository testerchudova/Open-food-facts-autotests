package qa.openfoodfacts.tests.web;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import qa.openfoodfacts.config.WebConfig;
import qa.openfoodfacts.helpers.Attach;

import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;

public class WebTestBase {

    private static final WebConfig CONFIG = ConfigFactory.create(WebConfig.class, System.getProperties());

    @BeforeAll
    static void setUp() {
        Configuration.baseUrl = CONFIG.baseUrl();
        Configuration.browser = CONFIG.browser();
        Configuration.browserSize = CONFIG.browserSize();
        Configuration.browserVersion = CONFIG.browserVersion();
        Configuration.headless = CONFIG.headless();
        Configuration.timeout = CONFIG.timeout();
        Configuration.pageLoadTimeout = CONFIG.pageLoadTimeout();

        configureRemoteBrowser();

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true));
    }

    @AfterEach
    void tearDown() {
        if (hasWebDriverStarted()) {
            Attach.screenshotAs("Last screenshot");
            Attach.pageSource();
            Attach.browserConsoleLogs();
            attachSelenoidVideo();
            Selenide.clearBrowserCookies();
            closeWebDriver();
        }
    }

    private static void configureRemoteBrowser() {
        if (CONFIG.remoteUrl().isBlank()) {
            return;
        }

        Configuration.remote = CONFIG.remoteUrl();

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("enableVNC", true);
        capabilities.setCapability("enableVideo", CONFIG.enableVideo());
        Configuration.browserCapabilities = capabilities;
    }

    private void attachSelenoidVideo() {
        if (CONFIG.remoteUrl().isBlank() || !CONFIG.enableVideo()) {
            return;
        }

        RemoteWebDriver driver = (RemoteWebDriver) getWebDriver();
        String videoUrl = CONFIG.videoStorageUrl() + driver.getSessionId() + ".mp4";
        Attach.selenoidVideo(videoUrl);
    }
}
