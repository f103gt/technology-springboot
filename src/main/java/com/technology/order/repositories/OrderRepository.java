package com.technology.order.repositories;

import com.technology.order.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, BigInteger> {
    @Query("""
            select o from Order o\s
            where o.orderStatus = :orderStatus
             """)
    List<Order> findOrdersWithOrderStatus(@Param("orderStatus") String orderStatus);

    Optional<Order> findOrderByUniqueIdentifier(String uniqueIdentifier);

    @Transactional
    @Modifying
    @Query("""
            update Order o
            set o.orderStatus = :orderStatus
            where o.id = :orderId
            """)
    void updateOrderStatusByOrderId(
            @Param("orderStatus") String orderStatus,
            @Param("orderId") BigInteger orderId);

    @Query("""
            select o from Order o where
            o.employeeActivity.employee.email = :employeeEmail
            and o.orderStatus = :orderStatus
            """)
    List<Order> findAllOrdersByEmployeeEmailAndOrderStatus(
            @Param("employeeEmail") String employeeEmail,
            String orderStatus);

}
