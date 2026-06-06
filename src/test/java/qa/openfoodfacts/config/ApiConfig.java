package qa.openfoodfacts.config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "system:properties",
        "classpath:config/api.properties"
})
public interface ApiConfig extends Config {

    @Key("apiBaseUrl")
    @DefaultValue("https://world.openfoodfacts.org")
    String apiBaseUrl();

    @Key("stagingBaseUrl")
    @DefaultValue("https://world.openfoodfacts.net")
    String stagingBaseUrl();

    @Key("defaultBarcode")
    @DefaultValue("3017620422003")
    String defaultBarcode();

    @Key("defaultSearchTerm")
    @DefaultValue("nutella")
    String defaultSearchTerm();

    @Key("userAgent")
    @DefaultValue("OpenFoodFactsAutotests/1.0 (education project; contact: qa@example.com)")
    String userAgent();
}
