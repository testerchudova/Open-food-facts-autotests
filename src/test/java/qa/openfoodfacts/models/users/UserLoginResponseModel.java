package qa.openfoodfacts.models.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLoginResponseModel {

    private String status;

    @JsonProperty("status_verbose")
    private String statusVerbose;

    @JsonProperty("user_id")
    private String userId;
}
