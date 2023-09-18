package com.technology.products.services;

import com.technology.products.exceptions.ProductNotFoundException;
import com.technology.products.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ProductServiceImplProductDeleteTest extends ProductServiceImplTest{
    private String productName;

    @BeforeEach
    void setUp() {
        productName = "Test Product";
    }

    @Test
    void deleteProduct_DeletesProduct() {
        //arrange (setUp)

        //act
        when(productRepository.findProductByProductName(productName))
                .thenReturn(Optional.of(new Product()));
        productService.deleteProduct(productName);

        //assert
        verify(productRepository,times(1))
                .deleteProductByProductName(productName);
    }

    @Test
    void deleteProduct_Throws_ProductNotFoundException() {
        //arrange(setUp)
        //act
        when(productRepository
                .findProductByProductName(productName))
                .thenReturn(Optional.empty());
        //assert
        assertThrows(ProductNotFoundException.class,
                () -> productService.deleteProduct(productName),
                "Category " + productName + " not found.");
    }

}
