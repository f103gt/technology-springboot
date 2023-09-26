package com.technology.cart.helpers;

import com.technology.cart.factories.CartItemFactory;
import com.technology.cart.models.Cart;
import com.technology.cart.models.CartItem;
import com.technology.product.exceptions.ProductNotFoundException;
import com.technology.product.models.Product;
import com.technology.security.adapters.SecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;

public class CartServiceHelper {
    public static SecurityUser getSecurityUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (SecurityUser) authentication.getPrincipal();
    }


    public static Optional<CartItem> findParticularCartItemOptional(Set<CartItem> cartItems, BigInteger productId) {
        return cartItems.stream()
                .filter(cartItem ->
                        cartItem.getProduct().getId().equals(productId))
                .findFirst();
    }

    public static void increaseCartItemQuantity(CartItem cartItem) {
        int quantity = cartItem.getQuantity() + 1;
        cartItem.setQuantity(quantity);
        cartItem.setFinalPrice(cartItem.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(quantity)));
    }

    public static void createNewCartItemIfProductExists(Optional<Product> productOptional,
                                                        BigInteger productId,
                                                        Cart cart) {
        productOptional.ifPresentOrElse(product ->
                        CartItemFactory.createCartItem(1, cart, product),
                () ->
                        new ProductNotFoundException(
                                "Product with id" + productId + " not found"));
    }
}
