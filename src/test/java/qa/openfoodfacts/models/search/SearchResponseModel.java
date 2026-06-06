package qa.openfoodfacts.models.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResponseModel {

    private Integer count;

    private Integer page;

    @JsonProperty("page_size")
    private Integer pageSize;

    private List<SearchProductModel> products;
}
