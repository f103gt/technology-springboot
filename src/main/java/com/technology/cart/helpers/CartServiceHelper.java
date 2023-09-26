package com.technology.cart.helpers;

import com.technology.cart.factories.CartFactory;
import com.technology.cart.models.Cart;
import com.technology.cart.repositories.CartRepository;
import com.technology.registration.models.User;
import com.technology.registration.repositories.UserRepository;
import com.technology.security.adapters.SecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CartServiceHelper {
    public static User getUserFromContext(UserRepository userRepository) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return userRepository.findUserByEmail(securityUser.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public static Cart getOrCreateCart(CartRepository cartRepository, UserRepository userRepository) {
        User user = getUserFromContext(userRepository);
        Cart cart = user.getCart();
        if (cart == null) {
            cart = CartFactory.createCart(user);
            cartRepository.save(cart);
            userRepository.save(user);
        }
        return cart;
    }

    public static void remove
}
