package com.technology.cart.services;

import com.technology.cart.helpers.CartServiceHelper;
import com.technology.cart.models.Cart;
import com.technology.cart.models.CartItem;
import com.technology.cart.repositories.CartRepository;
import com.technology.product.exceptions.ProductNotFoundException;
import com.technology.product.repositories.ProductRepository;
import com.technology.registration.models.User;
import com.technology.registration.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.technology.cart.helpers.CartServiceHelper.findParticularCartItemOptional;

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

    @Override
    @Transactional
    public void saveCart(BigInteger productId) {
        User user = CartServiceHelper.getUserFromContext(userRepository);
        Cart cart = CartServiceHelper.getOrCreateCart(user,cartRepository, userRepository);
        addProductToCart(productId, cart);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void deleteCartItem(BigInteger productId) {
        User user = CartServiceHelper.getUserFromContext(userRepository);
        Cart cart = user.getCart();
        Set<CartItem> cartItems = new HashSet<>(cart.getCartItems());
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

    @Override
    @Transactional
    public void deleteCart() {
        User user = CartServiceHelper.getUserFromContext(userRepository);
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
                        CartServiceHelper
                                .createNewCartItemIfProductExists(
                                        productRepository,
                                        productId,
                                        cart)

        );
    }
}
