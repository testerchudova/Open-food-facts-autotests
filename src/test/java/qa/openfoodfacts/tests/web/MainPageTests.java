package qa.openfoodfacts.tests.web;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.openfoodfacts.pages.MainPage;

@Epic("Open Food Facts")
@Feature("Главная страница")
@Issue("HOMEWORK-1611")
@Owner("Katy")
@Tag("ui")
class MainPageTests extends WebTestBase {

    private final MainPage mainPage = new MainPage();

    @Test
    @DisplayName("На главной странице есть поле поиска")
    void mainPageHasSearchInput() {
        mainPage.openPage()
                .shouldBeOpened();
    }

    @Test
    @DisplayName("Главную страницу можно открыть повторно в одной сессии")
    void mainPageCanBeOpenedRepeatedly() {
        mainPage.openPage()
                .shouldBeOpened()
                .openPage()
                .shouldBeOpened();
    }
}
