package qa.openfoodfacts.tests.api;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.openfoodfacts.api.ProductsApiClient;
import qa.openfoodfacts.config.ApiConfig;
import qa.openfoodfacts.models.products.ProductResponseModel;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Open Food Facts")
@Feature("API продуктов")
@Owner("Katy")
@Tag("api")
class ProductsApiTests {

    private static final ApiConfig CONFIG = ConfigFactory.create(ApiConfig.class, System.getProperties());
    private static final ProductsApiClient PRODUCTS_API_CLIENT = new ProductsApiClient();
    private static ProductResponseModel productResponse;

    @BeforeAll
    static void setUp() {
        productResponse = step("Получить продукт по штрихкоду", () ->
                PRODUCTS_API_CLIENT.getProductByBarcode(CONFIG.defaultBarcode()));
    }

    @Test
    @DisplayName("Продукт можно получить по штрихкоду")
    void productCanBeReceivedByBarcode() {
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
        step("Проверить данные продукта", () -> {
            assertThat(productResponse.getProduct().getBrands()).isNotBlank();
            assertThat(productResponse.getProduct().getNutriments()).isNotNull();
        });
    }

    @Test
    @Disabled("Публичный Open Food Facts может отвечать таймаутом на запросы с некорректным штрихкодом; проверка оставлена как опциональная негативная")
    @DisplayName("Некорректный штрихкод возвращает ошибку валидации")
    void invalidBarcodeReturnsValidationErrorResponse() {
        ProductResponseModel response = step("Получить продукт по некорректному штрихкоду", () ->
                PRODUCTS_API_CLIENT.getProductByBarcode("0000000000000"));

        step("Проверить ответ для некорректного штрихкода", () -> {
            assertThat(response.getStatus()).isZero();
            assertThat(response.getStatusVerbose()).containsIgnoringCase("invalid code");
        });
    }
}
