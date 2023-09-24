package com.technology.factory;

import com.technology.category.models.Category;
import com.technology.category.repositories.CategoryRepository;
import com.technology.product.models.Product;
import com.technology.product.repositories.ProductRepository;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TestProductFactory {
    public Product createProduct(BigInteger id, Category category, String name,
                                  String sku, Integer quantity, BigDecimal price){
        return Product.builder()
                .id(id)
                .category(category)
                .productName(name)
                .sku(sku)
                .quantity(quantity)
                .price(price)
                .build();
    }
    @Transactional
    public Product createProduct(BigInteger id, Category category, String name,
                                        String sku, Integer quantity, BigDecimal price,
                                        ProductRepository productRepository) {
        Product product = createProduct(id,category,name,sku,quantity,price);
        productRepository.save(product);
        return  product;
    }

    @Transactional
    public Product createProduct(BigInteger id, Category category, String name,
                                        String sku, Integer quantity, BigDecimal price,
                                        CategoryRepository categoryRepository){
        Product product = createProduct(id,category,name,sku,quantity,price);
        category.getProducts().add(product);
        categoryRepository.save(category);
        return  product;
    }
}
