package com.technology.order.repositories;

import com.technology.activity.models.Activity;
import com.technology.order.models.Order;
import com.technology.shift.models.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, BigInteger> {
    @Query("""
            select o from Order o\s
            where o.orderStatus = :orderStatus
             """)
    List<Order> findOrdersWithOrderStatus(@Param("orderStatus") String orderStatus);

}
