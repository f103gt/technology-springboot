package com.technology.products.repositories;

import com.technology.products.models.products.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, BigInteger> {
    void deleteAllByCategoryId(Integer categoryId);
    Optional<Product> findProductByProductName(String productName);
}
