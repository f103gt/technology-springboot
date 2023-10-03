package com.technology.order.repositories;

import com.technology.order.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, BigInteger> {
    @Query("""
            select o from Order o
            where o.orderStatus.statusName = :orderStatus
            order by o.id asc
            """)
    Optional<Order> findFirstOrOrderByOrderStatus(@Param("orderStatus") String orderStatus);

    @Query("""
            select o from Order o
            join o.cart c
            join c.cartItems ci
            where o.orderStatus = 'PLACED'
            group by o.id
            order by abs(sum(ci.quantity) - :requiredNumber) asc
            """)
    Optional<Order> findOrderWithNumberOfItemsClosestToRequired(@Param("requiredNumber") BigInteger requiredNumber);

    @Query("""
            select coalesce(count(o),0)
            from Order o
            where o.orderStatus = :orderStatus
            """)
    Long findNumberOfUnprocessedOrders(@Param("orderStatus") String orderStatus);
}
