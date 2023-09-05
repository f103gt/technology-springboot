package com.technology.products.services;

import com.technology.products.models.products.Product;

import java.util.Set;

public interface ProductService {
    void saveProduct(Product product);
    void saveProducts(Set<Product> products);
}
