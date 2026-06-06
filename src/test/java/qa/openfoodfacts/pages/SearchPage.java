package qa.openfoodfacts.pages;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class SearchPage {

    public SearchPage shouldContain(String expectedText) {
        $("body").shouldBe(visible).shouldHave(text(expectedText));
        return this;
    }

    public SearchPage shouldContainProductCard() {
        $("body").shouldBe(visible).shouldHave(text("product"));
        return this;
    }

    public SearchPage shouldShowNoResultMessage() {
        $("body").shouldBe(visible).shouldHave(text("No products"));
        return this;
    }
}
