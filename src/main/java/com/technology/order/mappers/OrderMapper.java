package com.technology.order.mappers;

import com.technology.cart.mappers.CartItemMapper;
import com.technology.order.dtos.OrderDto;
import com.technology.order.models.Order;
import com.technology.order.registration.requests.OrderRegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",uses = {CartItemMapper.class})
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "orderStatus",
            expression = "java(com.technology.order.models.OrderStatus.PENDING)")
    @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "employeeActivity", ignore = true)
    @Mapping(target = "uniqueIdentifier",ignore = true)
    Order orderRegistrationRequestToOrder(OrderRegistrationRequest request);

    @Mapping(source = "cart.cartItems",target = "cartItems")
    OrderDto orderToOrderDto(Order order);
}

