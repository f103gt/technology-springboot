package com.technology.product.factories;

import com.technology.category.models.Category;
import com.technology.product.models.Product;
import com.technology.product.registration.request.ProductRegistrationRequest;

public class ProductFactory {
    public static Product createProduct(Category category, ProductRegistrationRequest request){
        Product product = Product.builder()
                .category(category)
                .productName(request.getProductName())
                .sku(request.getSku())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .build();
        category.getProducts().add(product);
        return  product;
    }
}
