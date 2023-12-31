package com.technology.cart.services;

import com.technology.cart.dtos.CartItemDto;
import com.technology.user.models.User;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface CartService {
    void saveCart(BigInteger productId);
    void forceDeleteItemFromCart(String productName);

   /* void deleteCartItem(BigInteger productId);*/
   List<CartItemDto> saveAllCartItems(Map<String, String> cartItemRequests);

    void deleteItemFromCart(String productName);
    void addCartItem(String productName);
     List<CartItemDto> getUserCart();


    /*in deleteCart method no need to check if the
     * cart exists or not, cart purification/removal
     *  will be implemented as soon as the order
     * is successfully made */

    /* deleteCart method does not actually delete
    * all the items from the cart
    * only the relation between cart and use
    * is being destroyed, and, as a replacement
    * of it, a new relation between user order
    * and the cart is being begeted.
    * This approach helps to presume user oder history */
    //TODO if needed modify code to accept a parameter
    void deleteCart(User user);
}
