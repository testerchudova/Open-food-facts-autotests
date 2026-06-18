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

@Epic("Open Food Facts")
@Feature("Страница продукта")
@Issue("HOMEWORK-1611")
@Owner("Katy")
@Tag("ui")
class ProductPageTests extends WebTestBase {

    private final MainPage mainPage = new MainPage();

    @Test
    @DisplayName("Страница продукта отображает штрихкод из API")
    void productPageDisplaysPreparedProductBarcode() {
        PreparedProduct product = PreparedProductData.defaultProduct();

        mainPage.openProductByBarcode(product.barcode())
                .shouldContainBarcode(product.barcode());
    }

    @Test
    @DisplayName("Страница продукта отображает бренд из API")
    void productPageDisplaysPreparedProductBrand() {
        PreparedProduct product = PreparedProductData.defaultProduct();

        mainPage.openProductByBarcode(product.barcode())
                .shouldContainBrand(product.brand());
    }

    @Test
    @DisplayName("Страница продукта отображает название из API")
    void productPageDisplaysPreparedProductName() {
        PreparedProduct product = PreparedProductData.defaultProduct();

        mainPage.openProductByBarcode(product.barcode())
                .shouldContainProductName(product.productName());
    }

    @Test
    @DisplayName("Страница продукта содержит информацию о питательности")
    void productPageContainsNutritionInformation() {
        PreparedProduct product = PreparedProductData.defaultProduct();

        mainPage.openProductByBarcode(product.barcode())
                .shouldContainNutritionBlock();
    }
}
