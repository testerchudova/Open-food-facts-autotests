package qa.openfoodfacts.tests.api;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import qa.openfoodfacts.api.ProductsApiClient;
import qa.openfoodfacts.models.products.ProductUpdateRequestModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Open Food Facts")
@Feature("Подготовка продукта на staging")
@Issue("HOMEWORK-1611")
@Owner("Katy")
@Tag("api")
class StagingProductPreparationTests {

    private final ProductsApiClient productsApiClient = new ProductsApiClient();

    @Test
    @EnabledIfSystemProperty(named = "runStagingApi", matches = "true")
    @DisplayName("Черновик продукта можно подготовить на staging через API")
    void productDraftCanBePreparedOnStagingThroughApi() {
        String barcode = "200000" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmss"));

        ProductUpdateRequestModel product = ProductUpdateRequestModel.builder()
                .code(barcode)
                .productName("Autotest almond drink")
                .brands("QA Diploma")
                .categories("Plant-based foods and beverages")
                .quantity("1 l")
                .comment("Created by automated test data preparation")
                .build();

        step("Создать или обновить черновик продукта на staging", () ->
                productsApiClient.createOrUpdateProductOnStaging(product)
                        .then()
                        .statusCode(200));

        step("Проверить сгенерированный штрихкод", () ->
                assertThat(product.getCode()).isEqualTo(barcode));
    }
}
