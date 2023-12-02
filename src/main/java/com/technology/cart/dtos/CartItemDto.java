package com.technology.cart.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CartItemDto {
    private String categoryName;

    private String productName;

    private String productImage;

    private Integer productQuantity;

    private Integer cartItemQuantity;

    private BigDecimal cartItemPrice;
}


//private String sku;
/*TODO REPLACE UNIT PRICE WITH PRODUCT QUANTITY
       TO MAKE IT IMPOSSIBLE FOR USER TO ADD
       MORE PRODUCTS TO THE CART THAT IT IS
       CURRENTLY AVAILABLE*/
