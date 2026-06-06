package qa.openfoodfacts.mobile;

import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.assertj.core.api.Assertions.assertThat;

public class SearchScreen {

    private final By searchInput = AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.EditText\")");
    private final By searchInProgressMessage = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"Your search of\")");
    private final By resultList = AppiumBy.androidUIAutomator(
            "new UiSelector().scrollable(true)");

    public SearchScreen searchFor(String query) {
        SelenideElement input = $(searchInput).shouldBe(visible);
        input.click();
        clearCurrentSearchValue(input);
        input.sendKeys(query);
        ((AndroidDriver) getWebDriver()).pressKey(new KeyEvent(AndroidKey.ENTER));
        return this;
    }

    private void clearCurrentSearchValue(SelenideElement input) {
        String currentValue = input.getAttribute("text");
        if (currentValue == null || currentValue.isBlank()) {
            return;
        }

        AndroidDriver driver = (AndroidDriver) getWebDriver();
        for (int i = 0; i < currentValue.length(); i++) {
            driver.pressKey(new KeyEvent(AndroidKey.DEL));
        }
    }

    public SearchScreen shouldHaveResultContaining(String expectedText) {
        $(resultList).shouldBe(visible).shouldHave(text(expectedText));
        return this;
    }

    public SearchScreen shouldHaveSubmittedSearchFor(String expectedText) {
        $(searchInProgressMessage).shouldBe(visible);
        assertThat(getWebDriver().getPageSource()).contains(expectedText);
        return this;
    }
}
