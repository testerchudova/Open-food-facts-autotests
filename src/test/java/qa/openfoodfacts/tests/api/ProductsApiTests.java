package qa.openfoodfacts.tests.api;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Owner;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import qa.openfoodfacts.api.ProductsApiClient;
import qa.openfoodfacts.config.ApiConfig;
import qa.openfoodfacts.models.products.ProductResponseModel;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Open Food Facts")
@Feature("API продуктов")
@Issue("HOMEWORK-1611")
@Owner("Katy")
@Tag("api")
class ProductsApiTests {

    private static final ApiConfig CONFIG = ConfigFactory.create(ApiConfig.class, System.getProperties());
    private static final ProductsApiClient PRODUCTS_API_CLIENT = new ProductsApiClient();

    @Test
    @DisplayName("Продукт можно получить по штрихкоду")
    void productCanBeReceivedByBarcode() {
        ProductResponseModel productResponse = productResponse();

        step("Проверить успешный ответ по продукту", () -> {
            assertThat(productResponse.getStatus()).isEqualTo(1);
            assertThat(productResponse.getCode()).isEqualTo(CONFIG.defaultBarcode());
            assertThat(productResponse.getProduct()).isNotNull();
            assertThat(productResponse.getProduct().getProductName()).isNotBlank();
        });
    }

    @Test
    @DisplayName("Ответ продукта содержит бренд и нутриенты")
    void productResponseContainsBrandAndNutriments() {
        ProductResponseModel productResponse = productResponse();

        step("Проверить данные продукта", () -> {
            assertThat(productResponse.getProduct().getBrands()).isNotBlank();
            assertThat(productResponse.getProduct().getNutriments()).isNotNull();
        });
    }

    @Test
    @DisplayName("Raw-ответ продукта содержит ожидаемый штрихкод")
    void rawProductResponseContainsExpectedBarcode() {
        step("Получить raw-ответ продукта и проверить штрихкод", () -> {
            String code = PRODUCTS_API_CLIENT.getRawProductByBarcode(CONFIG.defaultBarcode())
                    .jsonPath()
                    .getString("code");

            assertThat(code).isEqualTo(CONFIG.defaultBarcode());
        });
    }

    @Test
    @EnabledIfSystemProperty(named = "runOptionalApi", matches = "true")
    @DisplayName("Некорректный штрихкод возвращает ошибку валидации")
    void invalidBarcodeReturnsValidationErrorResponse() {
        ProductResponseModel response = step("Получить продукт по некорректному штрихкоду", () ->
                PRODUCTS_API_CLIENT.getProductByBarcode("0000000000000"));

        step("Проверить ответ для некорректного штрихкода", () -> {
            assertThat(response.getStatus()).isZero();
            assertThat(response.getStatusVerbose()).containsIgnoringCase("invalid code");
        });
    }

    private static ProductResponseModel productResponse() {
        return step("Получить продукт по штрихкоду", () ->
                PRODUCTS_API_CLIENT.getProductByBarcode(CONFIG.defaultBarcode()));
    }
}
