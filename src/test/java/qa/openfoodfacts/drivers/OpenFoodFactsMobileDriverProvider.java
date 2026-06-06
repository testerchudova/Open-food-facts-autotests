package qa.openfoodfacts.drivers;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import qa.openfoodfacts.config.MobileConfig;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class OpenFoodFactsMobileDriverProvider implements WebDriverProvider {

    private static final MobileConfig CONFIG = ConfigFactory.create(MobileConfig.class, System.getProperties());

    @Override
    public WebDriver createDriver(Capabilities capabilities) {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", CONFIG.platformName());
        desiredCapabilities.setCapability("deviceName", CONFIG.deviceName());
        if (!CONFIG.udid().isBlank()) {
            desiredCapabilities.setCapability("udid", CONFIG.udid());
        }
        desiredCapabilities.setCapability("platformVersion", CONFIG.platformVersion());
        desiredCapabilities.setCapability("automationName", "UiAutomator2");
        desiredCapabilities.setCapability("autoGrantPermissions", true);
        desiredCapabilities.setCapability("noReset", CONFIG.noReset());
        desiredCapabilities.setCapability("adbExecTimeout", 120000);
        desiredCapabilities.setCapability("appWaitDuration", 120000);
        desiredCapabilities.setCapability("androidInstallTimeout", 120000);
        desiredCapabilities.setCapability("uiautomator2ServerInstallTimeout", 120000);
        desiredCapabilities.setCapability("uiautomator2ServerLaunchTimeout", 120000);

        if ("browserstack".equalsIgnoreCase(CONFIG.deviceHost())) {
            desiredCapabilities.setCapability("app", CONFIG.browserstackApp());
            desiredCapabilities.setCapability("bstack:options", browserstackOptions());
            return createAndroidDriver(CONFIG.browserstackUrl(), desiredCapabilities);
        }

        File app = new File(CONFIG.app());
        if (app.exists()) {
            desiredCapabilities.setCapability("app", app.getAbsolutePath());
        } else {
            desiredCapabilities.setCapability("appPackage", CONFIG.appPackage());
            desiredCapabilities.setCapability("appActivity", CONFIG.appActivity());
        }

        return createAndroidDriver(CONFIG.localUrl(), desiredCapabilities);
    }

    private static Map<String, Object> browserstackOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put("userName", CONFIG.userName());
        options.put("accessKey", CONFIG.accessKey());
        options.put("projectName", "Open Food Facts autotests");
        options.put("buildName", "qa-diploma");
        options.put("sessionName", "Android smoke tests");
        return options;
    }

    private static AndroidDriver createAndroidDriver(String remoteUrl, DesiredCapabilities capabilities) {
        try {
            return new AndroidDriver(new URL(remoteUrl), capabilities);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Incorrect Appium remote URL: " + remoteUrl, e);
        }
    }
}
