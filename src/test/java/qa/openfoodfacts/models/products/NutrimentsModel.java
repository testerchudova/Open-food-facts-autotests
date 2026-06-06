package qa.openfoodfacts.models.products;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NutrimentsModel {

    @JsonProperty("energy-kcal_100g")
    private Double energyKcal100g;

    @JsonProperty("fat_100g")
    private Double fat100g;

    @JsonProperty("carbohydrates_100g")
    private Double carbohydrates100g;

    @JsonProperty("proteins_100g")
    private Double proteins100g;

    @JsonProperty("salt_100g")
    private Double salt100g;

    @JsonProperty("sugars_100g")
    private Double sugars100g;
}
