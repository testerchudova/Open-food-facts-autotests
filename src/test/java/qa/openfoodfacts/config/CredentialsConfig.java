package qa.openfoodfacts.config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "system:properties",
        "classpath:config/credentials.properties"
})
public interface CredentialsConfig extends Config {

    @Key("userName")
    @DefaultValue("")
    String userName();

    @Key("password")
    @DefaultValue("")
    String password();
}
