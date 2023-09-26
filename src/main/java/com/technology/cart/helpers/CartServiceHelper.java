package com.technology.cart.helpers;

import com.technology.cart.factories.CartFactory;
import com.technology.cart.factories.CartItemFactory;
import com.technology.cart.models.Cart;
import com.technology.cart.models.CartItem;
import com.technology.cart.repositories.CartRepository;
import com.technology.product.exceptions.ProductNotFoundException;
import com.technology.product.models.Product;
import com.technology.product.repositories.ProductRepository;
import com.technology.registration.models.User;
import com.technology.registration.repositories.UserRepository;
import com.technology.security.adapters.SecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CartServiceHelper {
    public static User getUserFromContext(UserRepository userRepository) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return userRepository.findUserByEmail(securityUser.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public static Cart getOrCreateCart(User user,CartRepository cartRepository, UserRepository userRepository) {
        Cart cart = user.getCart();
        if (cart == null) {
            cart = CartFactory.createCart(user);
            cartRepository.save(cart);
            userRepository.save(user);
        }
        return cart;
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

    public static void createNewCartItemIfProductExists(ProductRepository productRepository,
                                                        BigInteger productId,
                                                        Cart cart) {
        Product product = productRepository.findProductById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product with id" + productId + " not found"));
        CartItemFactory.createCartItem(1, cart, product);
    }
}
