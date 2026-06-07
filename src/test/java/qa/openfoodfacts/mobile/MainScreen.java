package qa.openfoodfacts.mobile;

import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class MainScreen {

    private final By openedSearchInput = AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.EditText\")");
    private final List<By> continueButtons = List.of(
            AppiumBy.accessibilityId("Continue"),
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Continue\")"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Continue\")"),
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Next\")"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Next\")")
    );
    private final List<By> skipButtons = List.of(
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Skip\")"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Skip\")"),
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Later\")"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Later\")"),
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Not now\")"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Not now\")")
    );
    private final List<By> waitButtons = List.of(
            AppiumBy.androidUIAutomator("new UiSelector().text(\"Wait\")"),
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Wait\")"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Wait\")")
    );
    private final List<By> searchButtons = List.of(
            AppiumBy.accessibilityId("Search for a product"),
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Search\")"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Search\")"),
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"search\")"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"search\")"),
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Find\")"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Find\")")
    );
    private final List<By> scanButtons = List.of(
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Scan\")"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Scan\")"),
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"scan\")"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"scan\")"),
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Barcode\")"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Barcode\")"),
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"barcode\")"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"barcode\")")
    );

    public MainScreen skipOnboardingIfVisible() {
        for (int i = 0; i < 40; i++) {
            String pageSource = getWebDriver().getPageSource();
            if (waitIfApplicationIsNotResponding(pageSource)) {
                continue;
            }

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

            if (clickFirstVisible(continueButtons)) {
                sleep(700);
                continue;
            }

            if (clickFirstVisible(skipButtons)) {
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

    private boolean waitIfApplicationIsNotResponding(String pageSource) {
        if (!pageSource.contains("isn't responding")
                && !pageSource.contains("is not responding")
                && !pageSource.contains("isn\u2019t responding")) {
            return false;
        }

        if (clickFirstVisible(waitButtons)) {
            sleep(5000);
            return true;
        }

        return false;
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

        firstVisible(searchButtons).click();
        return new SearchScreen();
    }

    public MainScreen shouldHaveScanAction() {
        firstVisible(scanButtons);
        return this;
    }

    private boolean clickFirstVisible(List<By> locators) {
        for (By locator : locators) {
            try {
                SelenideElement element = $(locator);
                if (element.is(visible)) {
                    element.click();
                    return true;
                }
            } catch (WebDriverException ignored) {
                continue;
            }
        }

        return false;
    }

    private SelenideElement firstVisible(List<By> locators) {
        for (int attempt = 0; attempt < 30; attempt++) {
            waitIfApplicationIsNotResponding(getWebDriver().getPageSource());

            for (By locator : locators) {
                try {
                    SelenideElement element = $(locator);
                    if (element.is(visible)) {
                        return element;
                    }
                } catch (WebDriverException ignored) {
                    continue;
                }
            }

            sleep(500);
        }

        throw new AssertionError("No expected mobile element was found. Page source: " + shortPageSource());
    }

    private String shortPageSource() {
        String pageSource = getWebDriver().getPageSource()
                .replaceAll("\\s+", " ")
                .trim();

        return pageSource.length() > 1000 ? pageSource.substring(0, 1000) : pageSource;
    }
}
