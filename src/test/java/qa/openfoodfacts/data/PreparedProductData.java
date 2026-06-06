package qa.openfoodfacts.data;

import org.aeonbits.owner.ConfigFactory;
import qa.openfoodfacts.api.ProductsApiClient;
import qa.openfoodfacts.config.ApiConfig;
import qa.openfoodfacts.models.products.ProductModel;
import qa.openfoodfacts.models.products.ProductResponseModel;

public class PreparedProductData {

    private static final ApiConfig CONFIG = ConfigFactory.create(ApiConfig.class, System.getProperties());
    private static final ProductsApiClient PRODUCTS_API_CLIENT = new ProductsApiClient();

    private PreparedProductData() {
    }

    public static PreparedProduct defaultProduct() {
        try {
            ProductResponseModel response = PRODUCTS_API_CLIENT.getProductByBarcode(CONFIG.defaultBarcode());
            ProductModel product = response.getProduct();

            if (response.getStatus() != null && response.getStatus() == 1 && product != null) {
                return new PreparedProduct(
                        response.getCode(),
                        valueOrFallback(product.getProductName(), CONFIG.defaultSearchTerm()),
                        valueOrFallback(product.getBrands(), "Ferrero")
                );
            }
        } catch (Throwable ignored) {
        }

        return new PreparedProduct(CONFIG.defaultBarcode(), CONFIG.defaultSearchTerm(), "Ferrero");
    }

    private static String valueOrFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
