package qa.openfoodfacts.tests.mobile;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import qa.openfoodfacts.mobile.MainScreen;

@Epic("Open Food Facts")
@Feature("Главный экран Android-приложения")
@Issue("HOMEWORK-1611")
@Owner("Katy")
@Tag("mobile")
@EnabledIfSystemProperty(named = "runMobile", matches = "true")
class MobileMainScreenTests extends MobileTestBase {

    private final MainScreen mainScreen = new MainScreen();

    @Test
    @DisplayName("На главном экране есть действие сканирования")
    void mainScreenContainsScanAction() {
        openApp();
        mainScreen.skipOnboardingIfVisible()
                .shouldHaveScanAction();
    }

    @Test
    @DisplayName("На главном экране есть действие поиска продукта")
    void mainScreenContainsSearchAction() {
        openApp();
        mainScreen.skipOnboardingIfVisible()
                .shouldHaveSearchAction();
    }
}
