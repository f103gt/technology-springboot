package com.technology.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Getter
public class ProductDto {

    private String categoryName;
    private String productName;

    private String sku;
    private Integer quantity;
    private BigDecimal price;
}
