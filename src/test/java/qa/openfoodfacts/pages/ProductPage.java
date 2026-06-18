package qa.openfoodfacts.pages;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ProductPage {

    @Step("Проверить штрихкод продукта: {barcode}")
    public ProductPage shouldContainBarcode(String barcode) {
        $("body").shouldBe(visible).shouldHave(text(barcode));
        return this;
    }

    @Step("Проверить название продукта: {productName}")
    public ProductPage shouldContainProductName(String productName) {
        $("body").shouldBe(visible).shouldHave(text(productName));
        return this;
    }

    @Step("Проверить бренд продукта: {brand}")
    public ProductPage shouldContainBrand(String brand) {
        $("body").shouldBe(visible).shouldHave(text(brand));
        return this;
    }

    @Step("Проверить блок с пищевой ценностью")
    public ProductPage shouldContainNutritionBlock() {
        $("body").shouldBe(visible).shouldHave(text("Nutrition"));
        return this;
    }
}
