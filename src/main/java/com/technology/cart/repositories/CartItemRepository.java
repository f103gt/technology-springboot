package com.technology.cart.repositories;

import com.technology.cart.models.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, BigInteger> {
    @Modifying
    @Query(value = """
            UPDATE cart_item
            SET quantity = quantity - 1,
            price = price - (SELECT price FROM product WHERE id = :productId)
            WHERE cart_id = :cartId AND product_id = :productId
            """,nativeQuery = true)
    void deleteOrDecreaseCartItemByProductId(@Param("cartId") BigInteger cartId,
                                             @Param("productId") BigInteger productId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM CartItem ci WHERE ci.cart.id = :cardId AND ci.product.id = :productId")
    void deleteCartItemByProductId(@Param("cardId") BigInteger cardId,
                                   @Param("productId") BigInteger productId);

    Optional<CartItem> findCartItemByProductIdAndCartId(BigInteger productId, BigInteger cartId);
}
