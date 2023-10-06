package com.technology.cart.controllers;

import com.technology.cart.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{productId}")
    public ResponseEntity<String> addToCart(
            @PathVariable BigInteger productId) {
        cartService.saveCart(productId);
        return ResponseEntity
                .ok("The product was successfully added to the cart");
    }

    /*@PreAuthorize("hasRole('USER')")
    @DeleteMapping("/delete-cart")
    public ResponseEntity<String> deleteCart() {
        cartService.deleteCart();
        return ResponseEntity.ok("The cart was successfully deleted");
    }*/
}
