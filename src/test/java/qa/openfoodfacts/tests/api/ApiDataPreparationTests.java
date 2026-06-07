package qa.openfoodfacts.tests.api;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.openfoodfacts.data.PreparedProduct;
import qa.openfoodfacts.data.PreparedProductData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Open Food Facts")
@Feature("Подготовка данных через API")
@Issue("HOMEWORK-1611")
@Owner("Katy")
@Tag("api")
class ApiDataPreparationTests {

    @Test
    @DisplayName("API подготавливает стабильные данные продукта для UI-тестов")
    void apiPreparesStableProductDataForUiTests() {
        PreparedProduct product = step("Подготовить продукт через API-клиент", PreparedProductData::defaultProduct);

        step("Проверить подготовленный продукт", () -> {
            assertThat(product.barcode()).isNotBlank();
            assertThat(product.productName()).isNotBlank();
            assertThat(product.brand()).isNotBlank();
        });
    }
}
