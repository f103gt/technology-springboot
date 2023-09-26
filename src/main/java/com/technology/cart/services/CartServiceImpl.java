package com.technology.cart.services;

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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    private User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return userRepository.findUserByEmail(securityUser.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public void saveCart(BigInteger productId) {
        User user = getUserFromContext();
        Cart cart = user.getCart();
        if (cart == null) {
            cart = CartFactory.createCart(user);
            cartRepository.save(cart);
            userRepository.save(user);
        }
        addProductToCart(productId, cart);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void deleteCartItem(BigInteger productId) {
        User user = getUserFromContext();
        Cart cart = user.getCart();
        Set<CartItem> cartItems = new HashSet<>(cart.getCartItems());
        Optional<CartItem> cartItemToRemove = cartItems.stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst();
        if (cartItemToRemove.isPresent()) {
            cartItems.remove(cartItemToRemove.get());
            cartRepository.save(cart);
        } else {
            throw new ProductNotFoundException("Product with id " + productId + " not found");
        }

    }

    @Override
    @Transactional
    public void deleteCart(){
        User user = getUserFromContext();
        cartRepository.delete(user.getCart());
        user.setCart(null);
        userRepository.save(user);
    }

    private void addProductToCart(BigInteger productId, Cart cart) {
        Set<CartItem> cartItems = new HashSet<>(cart.getCartItems());
        Optional<CartItem> cartItemOptional = cartItems.stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst();
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            int quantity = cartItem.getQuantity() + 1;
            cartItem.setQuantity(quantity);
            cartItem.setFinalPrice(cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(quantity)));
        } else {
            Optional<Product> productOptional = productRepository.findProductById(productId);
            if (productOptional.isEmpty()) {
                throw new ProductNotFoundException("Product with id" + productId + " not found");
            }
            Product product = productOptional.get();
            CartItemFactory.createCartItem(1,cart,product);
        }
    }
}
