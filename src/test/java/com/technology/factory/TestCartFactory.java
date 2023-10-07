package com.technology.factory;

import com.technology.cart.models.Cart;
import com.technology.user.models.User;

import java.math.BigInteger;
import java.util.HashSet;

public class TestCartFactory {
    public static Cart createCart(int id, User user) {
        Cart cart = Cart.builder()
                .id(BigInteger.valueOf(id))
                .user(user)
                .cartItems(new HashSet<>())
                .build();
        user.setCart(cart);
        return cart;
    }
}
