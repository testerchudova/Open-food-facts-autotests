package qa.openfoodfacts.tests.web;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.openfoodfacts.data.PreparedProduct;
import qa.openfoodfacts.data.PreparedProductData;
import qa.openfoodfacts.pages.MainPage;

import static io.qameta.allure.Allure.step;

@Epic("Open Food Facts")
@Feature("Страница продукта")
@Issue("HOMEWORK-1611")
@Owner("Katy")
@Tag("ui")
class ProductPageTests extends WebTestBase {

    private final MainPage mainPage = new MainPage();

    @Test
    @DisplayName("Страница продукта отображает данные, подготовленные через API")
    void productPageDisplaysPreparedProductData() {
        PreparedProduct product = PreparedProductData.defaultProduct();

        step("Открыть страницу продукта по штрихкоду из API-данных", () ->
                mainPage.openProductByBarcode(product.barcode()));
        step("Проверить штрихкод продукта", () ->
                mainPage.openProductByBarcode(product.barcode()).shouldContainBarcode(product.barcode()));
        step("Проверить бренд продукта", () ->
                mainPage.openProductByBarcode(product.barcode()).shouldContainBrand(product.brand()));
    }

    @Test
    @DisplayName("Страница продукта содержит информацию о питательности")
    void productPageContainsNutritionInformation() {
        PreparedProduct product = PreparedProductData.defaultProduct();

        step("Открыть страницу продукта", () -> mainPage.openProductByBarcode(product.barcode()));
        step("Проверить блок с информацией о питательности", () ->
                mainPage.openProductByBarcode(product.barcode()).shouldContainNutritionBlock());
    }
}
