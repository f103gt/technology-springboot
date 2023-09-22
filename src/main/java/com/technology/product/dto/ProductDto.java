package com.technology.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class ProductDto {

    private String categoryName;
    private String productName;

    private String sku;
    private Integer quantity;
    private BigDecimal price;
}
