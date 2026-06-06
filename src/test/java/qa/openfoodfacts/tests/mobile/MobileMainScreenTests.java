package qa.openfoodfacts.tests.mobile;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import qa.openfoodfacts.mobile.MainScreen;

import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;

@Epic("Open Food Facts")
@Feature("Главный экран Android-приложения")
@Owner("Katy")
@Tag("mobile")
@EnabledIfSystemProperty(named = "runMobile", matches = "true")
class MobileMainScreenTests extends MobileTestBase {

    private final MainScreen mainScreen = new MainScreen();

    @Test
    @DisplayName("На главном экране есть действие сканирования")
    void mainScreenContainsScanAction() {
        step("Запустить мобильное приложение", () -> open());
        step("Пропустить онбординг, если он показан", () -> mainScreen.skipOnboardingIfVisible());
        step("Проверить действие сканирования", () -> mainScreen.shouldHaveScanAction());
    }
}
