package com.technology.order.models;

import com.technology.address.models.Address;
import com.technology.cart.models.Cart;
import com.technology.user.models.User;
import jakarta.persistence.*;
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
@Table(name = "shop_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="client_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    private Cart cart;

    @ManyToOne
    private OrderStatus orderStatus;

    @ManyToOne
    private PaymentMethod paymentMethod;

    @ManyToOne
    private DeliveryMethod deliveryMethod;

    @ManyToOne
    private Address deliveryAddress;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "total_price")
    private BigDecimal totalPrice;
}
