package qa.openfoodfacts.config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "system:properties",
        "classpath:config/web.properties"
})
public interface WebConfig extends Config {

    @Key("baseUrl")
    @DefaultValue("https://world.openfoodfacts.org")
    String baseUrl();

    @Key("browser")
    @DefaultValue("chrome")
    String browser();

    @Key("browserSize")
    @DefaultValue("1920x1080")
    String browserSize();

    @Key("browserVersion")
    @DefaultValue("")
    String browserVersion();

    @Key("remoteUrl")
    @DefaultValue("")
    String remoteUrl();

    @Key("enableVideo")
    @DefaultValue("true")
    boolean enableVideo();

    @Key("videoStorageUrl")
    @DefaultValue("https://selenoid.autotests.cloud/video/")
    String videoStorageUrl();

    @Key("headless")
    @DefaultValue("false")
    boolean headless();

    @Key("timeout")
    @DefaultValue("10000")
    long timeout();

    @Key("pageLoadTimeout")
    @DefaultValue("30000")
    long pageLoadTimeout();
}
