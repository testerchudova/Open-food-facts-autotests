package qa.openfoodfacts.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class MainPage {

    private final SelenideElement searchInput = $("[name='search_terms']");

    @Step("Открыть главную страницу")
    public MainPage openPage() {
        open("/");
        return this;
    }

    @Step("Проверить, что главная страница открыта")
    public MainPage shouldBeOpened() {
        searchInput.shouldBe(visible);
        return this;
    }

    @Step("Выполнить поиск: {query}")
    public SearchPage searchFor(String query) {
        searchInput.shouldBe(visible).setValue(query).pressEnter();
        return new SearchPage();
    }

    @Step("Открыть страницу продукта по штрихкоду: {barcode}")
    public ProductPage openProductByBarcode(String barcode) {
        open("/product/" + barcode);
        return new ProductPage();
    }
}
