/*
package com.technology.category.test.repositories;

import com.technology.product.repositories.ProductRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.Optional;

public interface TestProductRepository extends ProductRepository {
    @Query("""
            select product.id from Product product where product.id = (select max(p.id) from Product p)
            """)
    Optional<BigInteger> findMaxProductId();
}
*/
