package com.technology.order.mappers;

import com.technology.cart.mappers.CartItemMapper;
import com.technology.order.dtos.OrderDto;
import com.technology.order.models.DeliveryMethod;
import com.technology.order.models.Order;
import com.technology.order.models.OrderStatus;
import com.technology.order.models.PaymentMethod;
import com.technology.order.registration.requests.OrderRegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "orderStatus",
            expression = "java(com.technology.order.models.OrderStatus.PENDING)")
    @Mapping(target = "paymentMethod", expression = "java(mapPaymentMethod(request.getPaymentMethod()))")
    @Mapping(target = "deliveryMethod", expression = "java(mapDeliveryMethod(request.getDeliveryMethod()))")
    @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "employeeActivity", ignore = true)
    @Mapping(target = "uniqueIdentifier", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    Order orderRegistrationRequestToOrder(OrderRegistrationRequest request);

    default DeliveryMethod mapDeliveryMethod(String methodName) {
        if (methodName.toLowerCase().contains("meest")) {
            return DeliveryMethod.MEEST;
        }
        return DeliveryMethod.COURIER;
    }

    //TODO CREATE USER DATA DTO MAPPER
    default PaymentMethod mapPaymentMethod(String methodName) {
        if (methodName.toLowerCase().contains("card")) {
            return PaymentMethod.CARD;
        } else if (methodName.toLowerCase().contains("cash")) {
            return PaymentMethod.CASH;
        }
        return PaymentMethod.PAYPAL;
    }

    //TODO HOW CAN I TURN ORDER STATUS INTO STRING WHEN MAPPING
    @Mapping(target = "orderStatus", expression = "java(mapOrderStatus(order.getOrderStatus()))")
    @Mapping(target = "deliveryMethod", expression = "java(mapDeliveryMethod(order.getDeliveryMethod()))")
    @Mapping(target = "paymentMethod", expression = "java(mapPaymentMethod(order.getPaymentMethod()))")
    @Mapping(target = "orderDate", expression = "java(localDateTimeToString(order.getOrderDate()))")
    @Mapping(source = "cart.cartItems", target = "cartItems")
    @Mapping(target = "subTotal", source = "totalPrice")
    @Mapping(target = "totalPrice", expression = "java(getFinalPrice(order))")
    @Mapping(target = "deliveryPrice", expression = "java(getDeliverCosts(order))")
    OrderDto orderToOrderDto(Order order);

    default BigDecimal getFinalPrice(Order order) {
        return order.getTotalPrice().add(getDeliverCosts(order));
    }

    default BigDecimal getDeliverCosts(Order order) {
        return BigDecimal.valueOf(order.getDeliveryMethod().getCost());
    }

    default String localDateTimeToString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    default String mapOrderStatus(OrderStatus orderStatus) {
        if (orderStatus != null) {
            String word = orderStatus.name().toLowerCase();
            word = word.substring(0, 1).toUpperCase() + word.substring(1);
            return word;
        } else {
            return null;
        }
    }

    default String mapDeliveryMethod(DeliveryMethod deliveryMethod) {
        if (deliveryMethod != null) {
            String word = deliveryMethod.name().toLowerCase();
            word = word.substring(0, 1).toUpperCase() + word.substring(1);
            return word;
        } else {
            return null;
        }
    }

    default String mapPaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod != null) {
            String word = paymentMethod.name().toLowerCase();
            word = word.substring(0, 1).toUpperCase() + word.substring(1);
            return word;
        } else {
            return null;
        }
    }
}

