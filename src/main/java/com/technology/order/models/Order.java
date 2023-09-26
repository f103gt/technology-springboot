package com.technology.order.models;

import com.technology.cart.models.Cart;
import com.technology.registration.models.Address;
import com.technology.registration.models.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Order {
    private BigInteger id;

    private User user;

    private Cart cart;

    @Column("order_status")
    private OrderStatus orderStatus;

    @Column("payment_method")
    private PaymentMethod paymentMethod;

    @Column("delivery_method")
    private DeliveryMethod deliveryMethod;

    @Column("delivery_address")
    private Address deliveryAddress;

    @Column("order_date")
    private LocalDate orderDate;

    @Column("total_price")
    private BigDecimal totalPrice;

}
