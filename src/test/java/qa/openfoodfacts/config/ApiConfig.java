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
    @DefaultValue("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/125 Safari/537.36")
    String userAgent();
}
