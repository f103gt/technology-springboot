package com.technology.cart.repositories;

import com.technology.cart.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface CartRepository extends JpaRepository<Cart, BigDecimal> {
}
