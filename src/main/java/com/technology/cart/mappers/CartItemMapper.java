package com.technology.cart.mappers;

import com.technology.cart.dtos.CartItemDto;
import com.technology.cart.models.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(source = "product.productName",target = "productName")
    @Mapping(source = "product.sku",target = "sku")
    @Mapping(source = "product.primaryImageUrl",target = "primaryImageUrl")
    @Mapping(source = "product.price",target = "unitPrice")
    CartItemDto cartItemToCartItemDto(CartItem cartItem);
}
