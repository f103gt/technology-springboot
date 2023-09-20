package com.technology.factory;

import com.technology.category.models.Category;
import com.technology.products.models.Product;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TestObjectFactory {
    public static Category createCategory(Integer id, String name, Category parent) {
        return Category.builder()
                .id(id)
                .categoryName(name)
                .parentCategory(parent)
                .build();
    }

    public static Product createProduct(BigInteger id, Category category, String name,
                                        String sku, Integer quantity, BigDecimal price) {
        return Product.builder()
                .id(id)
                .category(category)
                .productName(name)
                .sku(sku)
                .quantity(quantity)
                .price(price)
                .build();
    }
}
