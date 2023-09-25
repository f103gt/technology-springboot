package com.technology.factory;

import com.technology.category.models.Category;
import com.technology.category.repositories.CategoryRepository;
import com.technology.product.models.Product;
import com.technology.product.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;

public class TestProductFactory {
    public static Product createProduct(){
        return Product.builder()
                .id(BigInteger.ONE)
                .productName("Test Product")
                .category(null)
                .sku("SKU1")
                .quantity(10)
                .price(BigDecimal.TEN)
                .cartItems(new HashSet<>())
                .images(new HashSet<>())
                .build();
    }
    public static Product createProduct(Category category, String name, String sku){
        Product product = Product.builder()
                .category(category)
                .productName(name)
                .sku(sku)
                .quantity(1)
                .price(BigDecimal.TEN)
                .cartItems(new HashSet<>())
                .build();
        category.getProducts().add(product);
        return product;
    }
}
