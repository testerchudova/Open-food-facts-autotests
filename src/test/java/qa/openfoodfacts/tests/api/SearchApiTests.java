package qa.openfoodfacts.tests.api;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Owner;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.openfoodfacts.api.SearchApiClient;
import qa.openfoodfacts.config.ApiConfig;
import qa.openfoodfacts.models.search.SearchResponseModel;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Open Food Facts")
@Feature("API поиска")
@Issue("HOMEWORK-1611")
@Owner("Katy")
@Tag("api")
@Disabled("Публичный search endpoint Open Food Facts может ограничивать анонимные Rest Assured-запросы; включать при наличии учетных данных или стабильной CI-сети")
class SearchApiTests {

    private static final ApiConfig CONFIG = ConfigFactory.create(ApiConfig.class, System.getProperties());
    private static final SearchApiClient SEARCH_API_CLIENT = new SearchApiClient();
    private static SearchResponseModel searchResponse;

    @BeforeAll
    static void setUp() {
        searchResponse = step("Один раз выполнить поиск продуктов для API-проверок", () ->
                SEARCH_API_CLIENT.searchProducts(CONFIG.defaultSearchTerm(), 3));
    }

    @Test
    @DisplayName("Эндпоинт поиска возвращает продукты по поисковому запросу")
    void searchEndpointReturnsProductsBySearchTerm() {
        step("Проверить ответ поиска", () -> {
            assertThat(searchResponse.getCount()).isPositive();
            assertThat(searchResponse.getProducts()).isNotEmpty();
            assertThat(searchResponse.getProducts())
                    .anySatisfy(product -> assertThat(product.getProductName()).isNotBlank());
        });
    }

    @Test
    @DisplayName("Эндпоинт поиска учитывает запрошенный размер страницы")
    void searchEndpointRespectsPageSize() {
        int pageSize = 3;

        step("Проверить размер списка продуктов", () ->
                assertThat(searchResponse.getProducts()).hasSizeLessThanOrEqualTo(pageSize));
    }
}
