package qa.openfoodfacts.tests.api;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Flaky;
import io.qameta.allure.Issue;
import io.qameta.allure.Owner;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import qa.openfoodfacts.api.SearchApiClient;
import qa.openfoodfacts.config.ApiConfig;
import qa.openfoodfacts.models.search.SearchResponseModel;

import java.net.SocketException;
import java.net.SocketTimeoutException;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@Epic("Open Food Facts")
@Feature("API поиска")
@Issue("HOMEWORK-1611")
@Owner("Katy")
@Tag("api")
@Tag("flaky")
class SearchApiTests {

    private static final ApiConfig CONFIG = ConfigFactory.create(ApiConfig.class, System.getProperties());
    private static final SearchApiClient SEARCH_API_CLIENT = new SearchApiClient();

    @Test
    @Flaky
    @DisplayName("Эндпоинт поиска возвращает продукты по поисковому запросу")
    void searchEndpointReturnsProductsBySearchTerm() {
        SearchResponseModel searchResponse = searchResponse();

        step("Проверить ответ поиска", () -> {
            assertThat(searchResponse.getCount()).isPositive();
            assertThat(searchResponse.getProducts()).isNotEmpty();
            assertThat(searchResponse.getProducts())
                    .anySatisfy(product -> assertThat(product.getProductName()).isNotBlank());
        });
    }

    @ParameterizedTest(name = "page_size={0}")
    @ValueSource(ints = {1, 3})
    @Flaky
    @DisplayName("Эндпоинт поиска учитывает запрошенный размер страницы")
    void searchEndpointRespectsPageSize(int pageSize) {
        SearchResponseModel searchResponse = searchResponse(pageSize);

        step("Проверить размер списка продуктов", () ->
                assertThat(searchResponse.getProducts()).hasSizeLessThanOrEqualTo(pageSize));
    }

    @Test
    @Flaky
    @DisplayName("POST-запрос к эндпоинту поиска возвращает продукты")
    void postSearchEndpointReturnsProducts() {
        SearchResponseModel searchResponse = searchResponseByPost(1);

        step("Проверить ответ POST-поиска", () -> {
            assertThat(searchResponse.getCount()).isPositive();
            assertThat(searchResponse.getProducts()).isNotEmpty();
            assertThat(searchResponse.getProducts().get(0).getProductName()).isNotBlank();
        });
    }

    private static SearchResponseModel searchResponse() {
        return searchResponse(3);
    }

    private static SearchResponseModel searchResponse(int pageSize) {
        return step("Выполнить поиск продуктов через API", () -> {
            try {
                return SEARCH_API_CLIENT.searchProducts(CONFIG.defaultSearchTerm(), pageSize);
            } catch (Throwable e) {
                assumeFalse(isSearchApiUnavailable(e),
                        "Open Food Facts search API is temporarily unavailable: " + shortErrorMessage(e));
                throw e;
            }
        });
    }

    private static SearchResponseModel searchResponseByPost(int pageSize) {
        return step("Выполнить POST-поиск продуктов через API", () -> {
            try {
                return SEARCH_API_CLIENT.searchProductsByPost(CONFIG.defaultSearchTerm(), pageSize);
            } catch (Throwable e) {
                assumeFalse(isSearchApiUnavailable(e),
                        "Open Food Facts search API is temporarily unavailable: " + shortErrorMessage(e));
                throw e;
            }
        });
    }

    private static boolean isSearchApiUnavailable(Throwable error) {
        Throwable current = error;
        while (current != null) {
            if (current instanceof SocketTimeoutException || current instanceof SocketException) {
                return true;
            }

            String message = current.getMessage();
            if (message != null && (message.contains("503") || message.contains("Service Unavailable"))) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }

    private static String shortErrorMessage(Throwable error) {
        Throwable current = error;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && !message.isBlank()) {
                return message.length() > 200 ? message.substring(0, 200) : message;
            }
            current = current.getCause();
        }

        return error.getClass().getSimpleName();
    }
}
