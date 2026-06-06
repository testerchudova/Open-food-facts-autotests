package qa.openfoodfacts.mobile;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.time.Duration;
import java.util.Collections;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class MainScreen {

    private final By continueButton = AppiumBy.accessibilityId("Continue");
    private final By nextButton = AppiumBy.accessibilityId("Next");
    private final By skipButton = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"Skip\")");
    private final By searchButton = AppiumBy.accessibilityId("Search for a product");
    private final By openedSearchInput = AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.EditText\")");
    private final By scanButton = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"Scan\")");

    public MainScreen skipOnboardingIfVisible() {
        for (int i = 0; i < 40; i++) {
            String pageSource = getWebDriver().getPageSource();
            if (pageSource.contains("Try it now!") || pageSource.contains("tap on any part of the card")) {
                tapOnboardingHintCloseButton();
                sleep(700);
                continue;
            }

            if (pageSource.contains("Camera access") || pageSource.contains("Authorize the access")) {
                tapCameraAccessLaterButton();
                sleep(700);
                continue;
            }

            if ($(continueButton).exists()) {
                $(continueButton).click();
                sleep(700);
                continue;
            }

            if ($(nextButton).exists()) {
                $(nextButton).click();
                sleep(700);
                continue;
            }

            if ($(skipButton).exists()) {
                $(skipButton).click();
                sleep(700);
                continue;
            }

            if (pageSource.contains("content-desc=\"Continue\"") || pageSource.contains("content-desc=\"Next\"")) {
                tapBottomRightActionButton();
                sleep(700);
                continue;
            }

            break;
        }

        returnToMainIfSearchScreenIsOpen();
        return this;
    }

    private void returnToMainIfSearchScreenIsOpen() {
        for (int i = 0; i < 2; i++) {
            if (!$(openedSearchInput).exists()) {
                return;
            }

            getWebDriver().navigate().back();
            sleep(700);
        }
    }

    private void tapBottomRightActionButton() {
        tap(0.80, 0.92);
    }

    private void tapOnboardingHintCloseButton() {
        tap(0.86, 0.77);
    }

    private void tapCameraAccessLaterButton() {
        tap(0.20, 0.88);
    }

    private void tap(double xRatio, double yRatio) {
        Dimension size = getWebDriver().manage().window().getSize();
        int x = (int) (size.getWidth() * xRatio);
        int y = (int) (size.getHeight() * yRatio);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        ((RemoteWebDriver) getWebDriver()).perform(Collections.singletonList(tap));
    }

    public SearchScreen openSearch() {
        if ($(openedSearchInput).exists()) {
            return new SearchScreen();
        }

        $(searchButton).shouldBe(visible).click();
        return new SearchScreen();
    }

    public MainScreen shouldHaveScanAction() {
        $(scanButton).shouldBe(visible);
        return this;
    }
}
