package com.technology.cart.repositories;

import com.technology.cart.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface CartItemRepository extends JpaRepository<CartItem, BigInteger> {
}
