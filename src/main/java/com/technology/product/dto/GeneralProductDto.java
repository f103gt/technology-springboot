package com.technology.product.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class GeneralProductDto {
    private String categoryName;
    private String productName;
    private BigDecimal price;
    private String primaryImage;
    private String quantity;
}

//TODO ADD PRODUCT MAPPER