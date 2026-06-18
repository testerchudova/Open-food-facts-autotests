package qa.openfoodfacts.config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "system:properties",
        "classpath:config/mobile.properties"
})
public interface MobileConfig extends Config {

    @Key("deviceHost")
    @DefaultValue("emulator")
    String deviceHost();

    @Key("platformName")
    @DefaultValue("Android")
    String platformName();

    @Key("deviceName")
    @DefaultValue("Pixel_4")
    String deviceName();

    @Key("udid")
    @DefaultValue("")
    String udid();

    @Key("platformVersion")
    @DefaultValue("17")
    String platformVersion();

    @Key("noReset")
    @DefaultValue("true")
    boolean noReset();

    @Key("localUrl")
    @DefaultValue("http://127.0.0.1:4723/wd/hub")
    String localUrl();

    @Key("browserstackUrl")
    @DefaultValue("https://hub.browserstack.com/wd/hub")
    String browserstackUrl();

    @Key("browserstackApp")
    @DefaultValue("")
    String browserstackApp();

    @Key("userName")
    @DefaultValue("")
    String userName();

    @Key("accessKey")
    @DefaultValue("")
    String accessKey();

    @Key("app")
    @DefaultValue("src/test/resources/apps/openfoodfacts-fdroid.apk")
    String app();

    @Key("appPackage")
    @DefaultValue("openfoodfacts.github.scrachx.openfood")
    String appPackage();

    @Key("appActivity")
    @DefaultValue("org.openfoodfacts.app.MainActivity")
    String appActivity();

    @Key("appWaitActivity")
    @DefaultValue("*")
    String appWaitActivity();

    @Key("appWaitForLaunch")
    @DefaultValue("false")
    boolean appWaitForLaunch();
}
