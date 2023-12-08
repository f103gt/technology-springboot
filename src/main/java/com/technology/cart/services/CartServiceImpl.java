package com.technology.cart.services;

import com.technology.cart.dtos.CartItemDto;
import com.technology.cart.exceptions.UserNotFoundException;
import com.technology.cart.helpers.CartServiceHelper;
import com.technology.cart.mappers.CartItemMapper;
import com.technology.cart.models.Cart;
import com.technology.cart.models.CartItem;
import com.technology.cart.repositories.CartItemRepository;
import com.technology.cart.repositories.CartRepository;
import com.technology.product.exceptions.ProductNotFoundException;
import com.technology.product.models.Product;
import com.technology.product.repositories.ProductRepository;
import com.technology.user.models.User;
import com.technology.user.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.technology.cart.helpers.CartServiceHelper.findParticularCartItemOptional;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;


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

    @Transactional
    @Override
    public List<CartItemDto> saveAllCartItems(Map<String, String> cartItemRequests) {
        BigInteger id = CartServiceHelper.getSecurityUserFromContext().getUser().getId();
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Cart cart = getOrCreateCart(user);

        cartItemRequests.forEach((productName, itemQuantity) -> {
            Product product = productRepository.findProductByProductName(productName)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found"));
            cartItemRepository
                    .findCartItemByProductIdAndCartId(product.getId(), cart.getId())
                    .ifPresentOrElse(cartItem ->
                            {
                                int newQuantity = cartItem.getQuantity() + Integer.parseInt(itemQuantity);
                                cartItem.setQuantity(newQuantity);
                                cartItem.setFinalPrice(product.getPrice()
                                        .multiply(BigDecimal.valueOf(newQuantity)));
                                cartItemRepository.save(cartItem);
                            },
                            () -> {
                                int quantity = Integer.parseInt(itemQuantity);
                                CartItem cartItem = CartItem.builder()
                                        .cart(cart)
                                        .quantity(quantity)
                                        .finalPrice(product.getPrice()
                                                .multiply(BigDecimal.valueOf(quantity)))
                                        .product(product)
                                        .build();
                                cart.getCartItems().add(
                                        cartItemRepository.saveAndFlush(cartItem));
                                cartRepository.save(cart);
                            }
                    );
        });

        return cart.getCartItems().stream()
                .map(cartItemMapper::cartItemToCartItemDto)
                .collect(Collectors.toList());

    }

    /*TODO CHECK IF USER IS ACTUALLY REGISTERED, AND ACTIVATED,
     *  IF USER HAS NOT ACTIVATED, RIGHT AWAY REDIRECT
     *  TO THE EMAIL CONFIRMATION*/
    @Override
    public void saveCart(BigInteger productId) {
        String userEmail = CartServiceHelper
                .getSecurityUserFromContext()
                .getUser()
                .getEmail();
        userRepository.findUserByEmail(userEmail)
                .ifPresent(user -> {
                    Cart cart = getOrCreateCart(user);
                    addProductToCart(productId, cart);
                    cartRepository.save(cart);
                });

    }

    /*deletes item from cart if the quantity is 1
    otherwise decreases item quantity*/
    @Override
    public void deleteItemFromCart(String productName) {
        Product product = productRepository.findProductByProductName(productName)
                .orElseThrow(() ->
                        new ProductNotFoundException("product not found"));
        String userEmail = CartServiceHelper.getSecurityUserFromContext().getUser().getEmail();
        userRepository.findUserByEmail(userEmail)
                .ifPresent(user -> {
                    Cart cart = user.getCart();
                    cartItemRepository.deleteOrDecreaseCartItemByProductId(cart.getId(), product.getId());
                });
    }

    /*deletes the provided item from cart
    regardless of the item quantity*/
    /*TODO FIND PRODUCT BY PRODUCT ID AND FIND CART BY USER ID
     *  INSTEAD OF RETRIEVING THE WHOLE OBJECTS */
    @Override
    @Transactional
    public void forceDeleteItemFromCart(String productName) {
        Product product = productRepository.findProductByProductName(productName)
                .orElseThrow(() ->
                        new ProductNotFoundException("product not found"));
        BigInteger userId = CartServiceHelper.getSecurityUserFromContext().getUser().getId();
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        Cart cart = user.getCart();
        cartItemRepository.deleteCartItemByProductId(cart.getId(), product.getId());
    }

    @Override
    public void deleteCart(User user) {
        //cartRepository.delete(user.getCart());
        user.setCart(null);
        userRepository.save(user);
    }

    private void addProductToCart(BigInteger productId, Cart cart) {
        List<CartItem> cartItems = cart.getCartItems().stream().toList();
        Optional<CartItem> cartItemOptional = findParticularCartItemOptional(cartItems, productId);
        //increaseQuantityOrAddNewCartItem
        cartItemOptional.ifPresentOrElse(
                CartServiceHelper::increaseCartItemQuantity,
                () ->
                {
                    Optional<Product> productOptional =
                            productRepository.findProductById(productId);
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
            cart = cartRepository.saveAndFlush(cart);
            userRepository.save(user);
        }
        return cart;
    }

}


//TODO DELETE ALL THE CART ITEMS OF THE PROVIDED PRODUCT
    /*@Override
    public void deleteCartItem(BigInteger productId) {
        String userEmail = CartServiceHelper.getSecurityUserFromContext().getUser().getEmail();
        userRepository.findUserByEmail(userEmail)
                .ifPresent(user -> {
                    Cart cart = user.getCart();
                    if (cart != null) {
                        List<CartItem> cartItems = cart.getCartItems().stream().toList();
                    }
                });

    }*/