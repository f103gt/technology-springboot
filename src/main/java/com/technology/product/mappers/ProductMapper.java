package com.technology.product.mappers;

import com.technology.product.dto.GeneralProductDto;
import com.technology.product.dto.ProductDto;
import com.technology.product.models.Product;
import com.technology.product.registration.request.ProductRegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "category.categoryName", target = "categoryName")
    ProductDto productToProductDto(Product product);

    @Mapping(source = "primaryImageUrl",target = "primaryImage")
    GeneralProductDto productToGeneralProductDto(Product product);
}
