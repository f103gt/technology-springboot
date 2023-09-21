package com.technology.cart.services;

import com.technology.cart.repositories.CartItemRepository;
import com.technology.cart.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public void saveCartItem(BigInteger productId){}
}
