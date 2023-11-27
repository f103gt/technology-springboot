package com.technology.product.mappers;

import com.technology.product.dto.GeneralProductDto;
import com.technology.product.dto.ProductDto;
import com.technology.product.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.categoryName", target = "categoryName")
    ProductDto productToProductDto(Product product);

    @Mapping(source = "primaryImageUrl",target = "primaryImage")
    @Mapping(source = "category.categoryName", target = "categoryName")
    GeneralProductDto productToGeneralProductDto(Product product);
}
