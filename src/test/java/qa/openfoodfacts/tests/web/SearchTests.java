package qa.openfoodfacts.tests.web;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.openfoodfacts.data.PreparedProduct;
import qa.openfoodfacts.data.PreparedProductData;
import qa.openfoodfacts.pages.MainPage;
import qa.openfoodfacts.pages.SearchPage;

import static io.qameta.allure.Allure.step;

@Epic("Open Food Facts")
@Feature("Поиск")
@Issue("HOMEWORK-1611")
@Owner("Katy")
@Tag("ui")
class SearchTests extends WebTestBase {

    private final MainPage mainPage = new MainPage();
    private final SearchPage searchPage = new SearchPage();

    @Test
    @Disabled("Open Food Facts может ограничивать повторные анонимные поисковые запросы; включать в CI при стабильном доступе")
    @DisplayName("Поиск по штрихкоду возвращает подготовленный продукт")
    void searchByBarcodeReturnsPreparedProduct() {
        PreparedProduct product = PreparedProductData.defaultProduct();

        step("Открыть главную страницу", () -> mainPage.openPage().shouldBeOpened());
        step("Выполнить поиск по штрихкоду, полученному из API", () -> mainPage.searchFor(product.barcode()));
        step("Проверить, что результаты поиска содержат штрихкод", () -> searchPage.shouldContain(product.barcode()));
    }

    @Test
    @Disabled("Open Food Facts может ограничивать повторные анонимные поисковые запросы; включать в CI при стабильном доступе")
    @DisplayName("Поиск по названию продукта возвращает карточки продуктов")
    void searchByProductNameReturnsProductCards() {
        PreparedProduct product = PreparedProductData.defaultProduct();

        step("Открыть главную страницу", () -> mainPage.openPage().shouldBeOpened());
        step("Выполнить поиск по названию продукта", () -> mainPage.searchFor(product.productName()));
        step("Проверить, что отображаются карточки продуктов", () -> searchPage.shouldContainProductCard());
    }

    @Test
    @Disabled("Open Food Facts может ограничивать повторные анонимные поисковые запросы; включать в CI при стабильном доступе")
    @DisplayName("Поиск по уникальному невалидному запросу показывает сообщение об отсутствии результатов")
    void searchForInvalidQueryShowsNoResultMessage() {
        step("Открыть главную страницу", () -> mainPage.openPage().shouldBeOpened());
        step("Выполнить поиск по невалидному запросу", () -> mainPage.searchFor("qa-autotest-no-product-0000000000"));
        step("Проверить сообщение об отсутствии результатов", () -> searchPage.shouldShowNoResultMessage());
    }
}
