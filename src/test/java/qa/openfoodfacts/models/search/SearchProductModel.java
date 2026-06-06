package qa.openfoodfacts.models.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchProductModel {

    private String code;

    @JsonProperty("product_name")
    private String productName;

    private String brands;

    private String categories;

    @JsonProperty("image_front_url")
    private String imageFrontUrl;
}
