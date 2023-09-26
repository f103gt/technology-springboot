package com.technology.cart.services;

import java.math.BigInteger;

public interface CartService {
    void saveCart(BigInteger productId);

    void deleteCartItem(BigInteger productId);

    /*in deleteCart method no need to check if the
     * cart exists or not, cart purification/removal
     *  will be implemented as soon as the order
     * is successfully made */
    //TODO if needed modify code to accept a parameter
    void deleteCart();
}
