package com.technology.cart.factories;

import com.technology.cart.models.Cart;
import com.technology.cart.models.CartItem;
import com.technology.product.models.Product;

public class CartItemFactory {
    public static CartItem createCartItem(int quantity, Cart cart, Product product){
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .quantity(quantity)
                .finalPrice(product.getPrice())
                .product(product)
                .build();
        cart.getCartItems().add(cartItem);
        product.getCartItems().add(cartItem);
        return cartItem;
    }
}
