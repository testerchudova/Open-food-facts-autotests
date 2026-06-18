package qa.openfoodfacts.pages;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class SearchPage {

    @Step("Проверить, что страница поиска доступна для проверки")
    public SearchPage skipIfTemporarilyUnavailable() {
        String pageText = $("body").shouldBe(visible).getText();

        assumeFalse(pageText.contains("Страница временно недоступна")
                        || pageText.contains("temporarily unavailable")
                        || pageText.contains("unusually high load"),
                "Open Food Facts search page is temporarily unavailable for anonymous users");
        return this;
    }

    @Step("Проверить, что результаты поиска содержат текст: {expectedText}")
    public SearchPage shouldContain(String expectedText) {
        $("body").shouldBe(visible).shouldHave(text(expectedText));
        return this;
    }
}
