package com.technology.cart.controllers;

import com.technology.cart.services.CartService;
import com.technology.user.dto.UserDto;
import com.technology.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final CartService cartService;
    private final UserService userService;


    @PreAuthorize("hasAnyRole('USER','STAFF','MANAGER','ADMIN')")
    @GetMapping("/get-user-data")
    public ResponseEntity<UserDto> getUserData(){
        return ResponseEntity.ok().body(userService.getUser());
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
