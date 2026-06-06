package qa.openfoodfacts.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class MainPage {

    private final SelenideElement searchInput = $("[name='search_terms']");

    public MainPage openPage() {
        open("/");
        return this;
    }

    public MainPage shouldBeOpened() {
        searchInput.shouldBe(visible);
        return this;
    }

    public SearchPage searchFor(String query) {
        searchInput.shouldBe(visible).setValue(query).pressEnter();
        return new SearchPage();
    }

    public ProductPage openProductByBarcode(String barcode) {
        open("/product/" + barcode);
        return new ProductPage();
    }
}
