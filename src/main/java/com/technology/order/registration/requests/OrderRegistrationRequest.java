package com.technology.order.registration.requests;

import com.technology.user.models.User;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderRegistrationRequest {

    private String firstName;

    private String lastName;

    private String phoneNumber;
    private String email;

    private String paymentMethod;

    private String deliveryMethod;

    private String deliveryAddress;

    private BigDecimal totalPrice;
}
