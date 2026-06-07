package qa.openfoodfacts.tests.mobile;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import qa.openfoodfacts.data.PreparedProduct;
import qa.openfoodfacts.data.PreparedProductData;
import qa.openfoodfacts.mobile.MainScreen;
import qa.openfoodfacts.mobile.SearchScreen;

import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;

@Epic("Open Food Facts")
@Feature("Поиск в Android-приложении")
@Issue("HOMEWORK-1611")
@Owner("Katy")
@Tag("mobile")
@EnabledIfSystemProperty(named = "runMobile", matches = "true")
class MobileSearchTests extends MobileTestBase {

    private final MainScreen mainScreen = new MainScreen();
    private final SearchScreen searchScreen = new SearchScreen();

    @Test
    @DisplayName("Мобильное приложение отправляет поиск продукта, подготовленного через API")
    void mobileAppCanSearchProductPreparedByApi() {
        PreparedProduct product = PreparedProductData.defaultProduct();

        step("Запустить мобильное приложение", () -> open());
        step("Открыть экран поиска", () -> mainScreen.skipOnboardingIfVisible().openSearch());
        step("Выполнить поиск продукта по названию", () -> searchScreen.searchFor(product.productName()));
        step("Проверить, что поиск отправлен", () -> searchScreen.shouldHaveSubmittedSearchFor(product.productName()));
    }
}
