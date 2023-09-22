package com.technology.cart.test.repositories;

import com.technology.cart.models.CartItem;
import com.technology.cart.repositories.CartItemRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface TestCartItemRepository extends CartItemRepository {
    Optional<CartItem> findCartItemById(BigInteger id);
}
