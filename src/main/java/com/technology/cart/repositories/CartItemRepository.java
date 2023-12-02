package com.technology.cart.repositories;

import com.technology.cart.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, BigInteger> {
    @Modifying
    @Query(value = """
            DELETE FROM cart_item WHERE cart_id = :cartId AND product_id = :productId AND quantity = 1;
            UPDATE cart_item SET quantity = quantity - 1 WHERE cart_id = :cartId
            AND product_id = :productId AND quantity > 1
            """,
            nativeQuery = true)
    void deleteOrDecreaseCartItemByProductId(@Param("cartId") BigInteger cartId, @Param("productId") BigInteger productId);

    Optional<CartItem> findCartItemByProductIdAndCartId(BigInteger productId, BigInteger cartId);
}
