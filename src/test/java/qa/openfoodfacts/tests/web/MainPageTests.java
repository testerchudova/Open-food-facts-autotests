package qa.openfoodfacts.tests.web;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.openfoodfacts.pages.MainPage;

import static io.qameta.allure.Allure.step;

@Epic("Open Food Facts")
@Feature("Главная страница")
@Owner("Katy")
@Tag("ui")
class MainPageTests extends WebTestBase {

    private final MainPage mainPage = new MainPage();

    @Test
    @DisplayName("На главной странице есть поле поиска")
    void mainPageHasSearchInput() {
        step("Открыть главную страницу", () -> mainPage.openPage());
        step("Проверить, что поле поиска отображается", () -> mainPage.shouldBeOpened());
    }
}
