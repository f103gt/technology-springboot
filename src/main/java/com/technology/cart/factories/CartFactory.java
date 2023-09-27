package com.technology.cart.factories;

import com.technology.cart.models.Cart;
import com.technology.user.registration.models.User;

import java.util.HashSet;

public class CartFactory {
    public static Cart createCart(User user){
        Cart cart = Cart.builder()
                .cartItems(new HashSet<>())
                .user(user)
                .build();
        user.setCart(cart);
        return cart;
    }
}
