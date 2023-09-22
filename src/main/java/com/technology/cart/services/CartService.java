package com.technology.cart.services;

import com.technology.cart.models.Cart;
import com.technology.cart.models.CartItem;
import com.technology.cart.repositories.CartItemRepository;
import com.technology.cart.repositories.CartRepository;
import com.technology.product.exceptions.ProductNotFoundException;
import com.technology.product.models.Product;
import com.technology.product.repositories.ProductRepository;
import com.technology.registration.models.User;
import com.technology.registration.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public void saveCart(BigInteger productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Cart cart = user.getCart();
        if (cart == null) {
            cart = Cart.builder()
                    .cartItems(new HashSet<>())
                    .user(user)
                    .build();
            cartRepository.save(cart);
            user.setCart(cart);
            userRepository.save(user);
        }
        addProductToCart(productId, cart);
        cartRepository.save(cart);
    }

    private void addProductToCart(BigInteger productId, Cart cart) {
        Set<CartItem> cartItems = new HashSet<>(cart.getCartItems());
        Optional<CartItem> cartItemOptional = cartItems.stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst();
        if(cartItemOptional.isPresent()){
            CartItem cartItem = cartItemOptional.get();
            int quantity = cartItem.getQuantity() + 1;
            cartItem.setQuantity(quantity);
            cartItem.setFinalPrice(cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(quantity)));
        }
        else{
            Optional<Product> productOptional = productRepository.findProductById(productId);
            if (productOptional.isEmpty()) {
                throw new ProductNotFoundException("Product with id" + productId + " not found");
            }
            Product product = productOptional.get();
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .quantity(1)
                    .finalPrice(product.getPrice())
                    .product(product)
                .build();
            cart.getCartItems().add(cartItem);
        }
    }
}
