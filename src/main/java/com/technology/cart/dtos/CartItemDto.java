package com.technology.cart.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CartItemDto {

    private Integer quantity;

    private BigDecimal finalPrice;

    private String productName;

    private String sku;

    private String primaryImageUrl;
}
