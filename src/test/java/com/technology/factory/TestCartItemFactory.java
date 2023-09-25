package com.technology.factory;

import com.technology.cart.models.CartItem;
import com.technology.product.models.Product;

import java.math.BigInteger;

public class TestCartItemFactory {
    public static CartItem makeCartItem(Product product) {
        return CartItem.builder()
                .id(BigInteger.TEN)
                .product(product)
                .quantity(1)
                .finalPrice(product.getPrice())
                .build();
    }
}
