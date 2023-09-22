package com.technology.cart.repositories;

import com.technology.cart.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, BigDecimal> {
    Optional<Cart> findCartByUserId(BigInteger userId);
    void deleteCartByUserId(BigInteger userId);

    //transfer to test cart repository
    Optional<Cart> findCartById(BigInteger cartId);
}
