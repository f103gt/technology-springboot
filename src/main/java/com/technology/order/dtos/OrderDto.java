package com.technology.order.dtos;

import com.technology.cart.dtos.CartItemDto;
import com.technology.order.models.DeliveryMethod;
import com.technology.order.models.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OrderDto {
    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private String paymentMethod;

    private String deliveryMethod;

    private String deliveryAddress;

    private String orderDate;

    private String orderStatus;

    private BigDecimal subTotal;

    private BigDecimal deliveryPrice;

    private BigDecimal totalPrice;

    private String uniqueIdentifier;

    private List<CartItemDto> cartItems;
}
