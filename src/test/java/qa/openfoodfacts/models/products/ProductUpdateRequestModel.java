package qa.openfoodfacts.models.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequestModel {

    private String code;

    @JsonProperty("product_name")
    private String productName;

    private String brands;

    private String categories;

    private String quantity;

    private String comment;
}
