package qa.openfoodfacts.mobile;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ProductScreen {

    public ProductScreen shouldContainProductName(String productName) {
        $("body").shouldBe(visible).shouldHave(text(productName));
        return this;
    }

    public ProductScreen shouldContainNutritionTab() {
        $("body").shouldBe(visible).shouldHave(text("Nutrition"));
        return this;
    }
}
