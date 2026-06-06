package qa.openfoodfacts.models.products;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductModel {

    private String code;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("generic_name")
    private String genericName;

    private String brands;

    private String categories;

    private String quantity;

    @JsonProperty("image_front_url")
    private String imageFrontUrl;

    @JsonProperty("nutriscore_grade")
    private String nutriscoreGrade;

    private NutrimentsModel nutriments;
}
