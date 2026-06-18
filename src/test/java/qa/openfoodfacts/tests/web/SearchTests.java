package qa.openfoodfacts.tests.web;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Flaky;
import io.qameta.allure.Issue;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.openfoodfacts.data.PreparedProduct;
import qa.openfoodfacts.data.PreparedProductData;
import qa.openfoodfacts.pages.MainPage;
import qa.openfoodfacts.pages.SearchPage;

@Epic("Open Food Facts")
@Feature("Поиск")
@Issue("HOMEWORK-1611")
@Owner("Katy")
@Tag("ui")
@Tag("flaky")
class SearchTests extends WebTestBase {

    private final MainPage mainPage = new MainPage();
    private final SearchPage searchPage = new SearchPage();

    @Test
    @Flaky
    @DisplayName("Поиск по штрихкоду возвращает подготовленный продукт")
    void searchByBarcodeReturnsPreparedProduct() {
        PreparedProduct product = PreparedProductData.defaultProduct();

        mainPage.openPage()
                .shouldBeOpened()
                .searchFor(product.barcode());
        searchPage.skipIfTemporarilyUnavailable()
                .shouldContain(product.barcode());
    }

}
