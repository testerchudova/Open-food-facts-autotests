package qa.openfoodfacts.models.products;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponseModel {

    private String code;

    private Integer status;

    @JsonProperty("status_verbose")
    private String statusVerbose;

    private ProductModel product;
}
