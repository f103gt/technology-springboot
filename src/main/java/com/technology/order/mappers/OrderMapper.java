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

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "orderStatus",
            expression = "java(com.technology.order.models.OrderStatus.PENDING)")
    @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "employeeActivity", ignore = true)
    @Mapping(target = "uniqueIdentifier", ignore = true)
    Order orderRegistrationRequestToOrder(OrderRegistrationRequest request);

    //TODO HOW CAN I TURN ORDER STATUS INTO STRING WHEN MAPPING
    @Mapping(target = "orderStatus", expression = "java(mapOrderStatus(order.getOrderStatus()))")
    @Mapping(target = "deliveryMethod", expression = "java(mapDeliveryMethod(order.getDeliveryMethod()))")
    @Mapping(target = "paymentMethod", expression = "java(mapPaymentMethod(order.getPaymentMethod()))")
    @Mapping(source = "cart.cartItems", target = "cartItems")
    OrderDto orderToOrderDto(Order order);


    default String mapOrderStatus(OrderStatus orderStatus) {
        return orderStatus != null ? orderStatus.name() : null;
    }
    default String mapDeliveryMethod(DeliveryMethod deliveryMethod) {
        return deliveryMethod != null ? deliveryMethod.name() : null;
    }
    default String mapPaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethod != null ? paymentMethod.name() : null;
    }
}

