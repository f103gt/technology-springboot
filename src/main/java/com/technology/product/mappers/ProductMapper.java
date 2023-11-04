package com.technology.product.mappers;

import com.technology.product.dto.ProductDto;
import com.technology.product.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "category.categoryName", target = "categoryName")
    ProductDto productToProductDto(Product product);
}
