package com.technology.cart.controller;

import com.technology.cart.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
public class UserController {
    private final CartService cartService;

            @Autowired
    public UserController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/user/{productId}")
    public ResponseEntity<String> addToCart(
            @PathVariable BigInteger productId) {
        cartService.saveCart(productId);
        return ResponseEntity.ok("The product was successfully added");
    }
}
