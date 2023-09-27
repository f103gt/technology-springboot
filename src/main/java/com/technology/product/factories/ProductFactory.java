package com.technology.product.factories;

import com.technology.category.models.Category;
import com.technology.product.dto.ProductDto;
import com.technology.product.models.Product;
import com.technology.product.registration.request.ProductRegistrationRequest;

public class ProductFactory {
    public static Product createProduct(Category category, ProductRegistrationRequest request){
        return Product.builder()
                .category(category)
                .productName(request.getProductName().trim())
                .sku(request.getSku().trim())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .build();
    }

    public static ProductDto createProductDto(Product product){
        return new ProductDto(
                product.getCategory().getCategoryName(),
                product.getProductName(),
                product.getSku(),
                product.getQuantity(),
                product.getPrice());
    }
}
