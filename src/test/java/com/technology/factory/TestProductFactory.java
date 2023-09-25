package com.technology.factory;

import com.technology.category.models.Category;
import com.technology.category.repositories.CategoryRepository;
import com.technology.product.models.Product;
import com.technology.product.repositories.ProductRepository;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;

public class TestProductFactory {
    public static Product createProduct(BigInteger id, Category category, String name,
                                  String sku, Integer quantity, BigDecimal price){
        return Product.builder()
                .id(id)
                .category(category)
                .productName(name)
                .sku(sku)
                .quantity(quantity)
                .price(price)
                .cartItems(new HashSet<>())
                .build();
    }
}
