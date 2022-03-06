package github.abhinavofficial.kafka.pluralsight.model;

import github.abhinavofficial.kafka.pluralsight.enums.Color;
import github.abhinavofficial.kafka.pluralsight.enums.DesignType;
import github.abhinavofficial.kafka.pluralsight.enums.ProductType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {
    private Color color;
    private ProductType type;
    private DesignType designType;
}
