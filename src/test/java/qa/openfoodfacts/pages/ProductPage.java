package qa.openfoodfacts.pages;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ProductPage {

    public ProductPage shouldContainBarcode(String barcode) {
        $("body").shouldBe(visible).shouldHave(text(barcode));
        return this;
    }

    public ProductPage shouldContainProductName(String productName) {
        $("body").shouldBe(visible).shouldHave(text(productName));
        return this;
    }

    public ProductPage shouldContainBrand(String brand) {
        $("body").shouldBe(visible).shouldHave(text(brand));
        return this;
    }

    public ProductPage shouldContainNutritionBlock() {
        $("body").shouldBe(visible).shouldHave(text("Nutrition"));
        return this;
    }
}
