package com.technology.order.models;

import com.technology.activity.models.Activity;
import com.technology.cart.models.Cart;
import com.technology.user.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
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

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    // TODO create phone number (country code) api
    @Column(name="phone_number")
    private String phoneNumber;

    private String email;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    private Cart cart;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private DeliveryMethod deliveryMethod;

    @Column(name="delivery_address")
    private String deliveryAddress;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name="unique_identifier")
    private String uniqueIdentifier;

    @ManyToOne
    private Activity employeeActivity;
}
//TODO establish lazy fetch for user because not all users can
// TODO have orders, employees only process the orders