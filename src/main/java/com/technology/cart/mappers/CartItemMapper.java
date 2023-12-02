package com.technology.cart.mappers;

import com.technology.cart.dtos.CartItemDto;
import com.technology.cart.models.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(source = "product.productName",target = "productName")
    @Mapping(source = "product.category.categoryName",target = "categoryName")
    @Mapping(source = "product.primaryImageUrl",target = "productImage")
    @Mapping(source="product.quantity",target = "productQuantity")
    @Mapping(source = "finalPrice", target = "cartItemPrice")
    @Mapping(source = "quantity",target = "cartItemQuantity")
    CartItemDto cartItemToCartItemDto(CartItem cartItem);
}
