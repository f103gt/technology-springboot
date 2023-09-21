package com.technology.product.repositories;

import com.technology.product.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, BigInteger> {
    //TODO add queries
    void deleteProductByProductName(String productName);

    Set<Product> findProductsByCategoryId(Integer categoryId);

    Optional<Product> findProductByProductName(String productName);

    @Query("""
            select p from Product p where p.id = :productId
            """)
    Optional<Product> findProductById(BigInteger productId);
}
