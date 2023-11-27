package com.technology.cart.services;

import com.technology.cart.dtos.CartItemDto;
import com.technology.cart.factories.CartFactory;
import com.technology.cart.helpers.CartServiceHelper;
import com.technology.cart.mappers.CartItemMapper;
import com.technology.cart.models.Cart;
import com.technology.cart.models.CartItem;
import com.technology.cart.repositories.CartRepository;
import com.technology.product.exceptions.ProductNotFoundException;
import com.technology.product.models.Product;
import com.technology.product.repositories.ProductRepository;
import com.technology.user.models.User;
import com.technology.user.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.technology.cart.helpers.CartServiceHelper.findParticularCartItemOptional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemMapper cartItemMapper;


    @Transactional
    public List<CartItemDto> getUserCart() {
        BigInteger userId = CartServiceHelper.getSecurityUserFromContext().getUser().getId();
        return cartRepository.findCartByUserId(userId)
                .map(cart -> cart.getCartItems().stream()
                        .map(cartItemMapper::cartItemToCartItemDto)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    @Transactional
    public void addCartItem(String productName) {
        Product product = productRepository.findProductByProductName(productName)
                .orElseThrow(() ->
                        new ProductNotFoundException(productName + " product not found"));
        String userEmail = CartServiceHelper.getSecurityUserFromContext().getUser().getEmail();
        userRepository.findUserByEmail(userEmail)
                .ifPresent(user -> {
                    Cart cart = getOrCreateCart(user);
                    addProductToCart(product.getId(), cart);
                    cartRepository.save(cart);
                });
    }

    @Override
    public void saveCart(BigInteger productId) {
        String userEmail = CartServiceHelper.getSecurityUserFromContext().getUser().getEmail();
        userRepository.findUserByEmail(userEmail)
                .ifPresent(user -> {
                    Cart cart = getOrCreateCart(user);
                    addProductToCart(productId, cart);
                    cartRepository.save(cart);
                });

    }

    @Override
    public void deleteItemFromCart(String productName) {
        Product product = productRepository.findProductByProductName(productName)
                .orElseThrow(() ->
                        new ProductNotFoundException("product not found"));
        deleteCartItem(product.getId());
    }

    @Override
    public void deleteCartItem(BigInteger productId) {
        String userEmail = CartServiceHelper.getSecurityUserFromContext().getUser().getEmail();
        userRepository.findUserByEmail(userEmail)
                .ifPresent(user -> {
                    Cart cart = user.getCart();
                    if (cart != null) {
                        Set<CartItem> cartItems = new HashSet<>(cart.getCartItems());
                        removeCartItemFromCartIfPresent(cartItems, cart, productId);
                    }
                });

    }

    @Override
    public void deleteCart(User user) {
        //cartRepository.delete(user.getCart());
        user.setCart(null);
        userRepository.save(user);
    }

    private void addProductToCart(BigInteger productId, Cart cart) {
        Set<CartItem> cartItems = new HashSet<>(cart.getCartItems());
        Optional<CartItem> cartItemOptional = findParticularCartItemOptional(cartItems, productId);
        //increaseQuantityOrAddNewCartItem
        cartItemOptional.ifPresentOrElse(
                CartServiceHelper::increaseCartItemQuantity,
                () ->
                {
                    Optional<Product> productOptional = productRepository.findProductById(productId);
                    CartServiceHelper.createNewCartItemIfProductExists(productOptional, productId, cart);
                }
        );
    }

    private Cart getOrCreateCart(User user) {
        Cart cart = user.getCart();
        if (cart == null) {
            cart = Cart.builder()
                    .cartItems(new HashSet<>())
                    .user(user)
                    .build();
            user.setCart(cart);
            cartRepository.save(cart);
            userRepository.save(user);
        }
        return cart;
    }

    private void removeCartItemFromCartIfPresent(Set<CartItem> cartItems, Cart cart, BigInteger productId) {
        Optional<CartItem> cartItemToRemoveOptional = findParticularCartItemOptional(cartItems, productId);
        cartItemToRemoveOptional.ifPresentOrElse(cartItemToRemove -> {
                    cartItems.remove(cartItemToRemove);
                    cartRepository.save(cart);
                },
                () -> {
                    throw new ProductNotFoundException(
                            "Product with id " + productId + " not found");
                }
        );
    }

}


