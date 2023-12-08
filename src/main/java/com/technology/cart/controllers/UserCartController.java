package com.technology.cart.controllers;

import com.technology.cart.dtos.CartItemDto;
import com.technology.cart.services.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/cart/api/v1")
@RequiredArgsConstructor
@Log4j2
public class UserCartController {
    private final CartService cartService;

    @GetMapping("/get-user-cart")
    public ResponseEntity<List<CartItemDto>> getUserCart() {
        return ResponseEntity.ok().body(cartService.getUserCart());
    }

    @PostMapping("/add-item")
    public ResponseEntity<String> addItemToCart(
            @RequestParam("cartItemName") String cartItemName) {
        cartService.addCartItem(cartItemName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add-all-cart-items")
    public ResponseEntity<List<CartItemDto>> addAllCartItemsToCart(
            @RequestBody Map<String, String> productQuantityMap) {
        log.atInfo().log("IN addAllCartItemsToCart");
        return ResponseEntity.ok()
                .body(cartService
                        .saveAllCartItems(productQuantityMap));
    }

    @DeleteMapping("/remove-item")
    public ResponseEntity<String> removeItemFromCart(@RequestParam("cartItemName") String cartItemName) {
        cartService.forceDeleteItemFromCart(cartItemName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-item")
    public ResponseEntity<String> deleteItemFromCart(
            @RequestParam("cartItemName") String cartItemName) {
        cartService.deleteItemFromCart(cartItemName);
        return ResponseEntity.ok().build();
    }
}
