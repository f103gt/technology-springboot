package com.technology.product.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class GeneralProductDto {
    private String productName;
    private BigDecimal productPrice;
    //private Image coverImage;
}
