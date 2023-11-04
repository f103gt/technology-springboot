package com.technology.product.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductDto {

    private String categoryName;
    private String productName;
    private String sku;
    private Integer quantity;
    private BigDecimal price;
    private String descriptionUrl;
    private String primaryImageUrl;
    private List<String> imageUrls;
}
