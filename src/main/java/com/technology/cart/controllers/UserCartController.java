package com.technology.cart.controllers;

import com.technology.cart.services.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
@RequestMapping("cart/api/v1")
@RequiredArgsConstructor
public class UserCartController {
    private final CartService cartService;

    @PostMapping("/add-item")
    public ResponseEntity<String> addItemToCart(
            @RequestParam("cartItemName") String cartItemName){
        cartService.addCartItem(cartItemName);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/delete-item")
    public ResponseEntity<String> deleteItemFromCart(
            @RequestParam("cartItemName") String cartItemName,
            @NonNull HttpServletRequest request) {
        cartService.deleteItemFromCart(cartItemName);
        return ResponseEntity.ok().build();
    }
}
