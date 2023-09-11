package com.technology.products.repositories;

import com.technology.products.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, BigInteger> {
    //TODO add queries
    void deleteAllByCategoryId(Integer categoryId);
    void deleteProductByProductName(String productName);
    Optional<Product> findProductByProductName(String productName);
}
