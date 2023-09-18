package com.technology.products.services;

import com.technology.category.exceptions.CategoryNotFoundException;
import com.technology.category.models.Category;
import com.technology.products.exceptions.ProductObjectAlreadyExistsException;
import com.technology.products.models.Product;
import com.technology.products.registration.request.ProductRegistrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class ProductServiceImplSaveProductTest extends ProductServiceImplTest {
    private ProductRegistrationRequest request;

    @BeforeEach
    void setUp() {
        request =
                new ProductRegistrationRequest(
                        "Category",
                        "Product 1",
                        "SKU1",
                        1,
                        BigDecimal.TEN
                );
    }

    @Test
    void saveProduct_SavesProduct() {
        //arrange setUp

        //act
        when(categoryRepository.findCategoryByCategoryName(
                request.getCategoryName()
        )).thenReturn(Optional.of(new Category()));

        when(productRepository.findProductByProductName(
                request.getProductName()
        )).thenReturn(Optional.empty());

        productService.saveProduct(request);

        //assert
        verify(productRepository, times(1))
                .save(any(Product.class));
    }

    @Test
    void saveProduct_Throws_CategoryNotFoundException() {
        //arrange setUp

        when(categoryRepository.findCategoryByCategoryName(
                request.getCategoryName()
        )).thenReturn(Optional.empty());

        //act and assert
        assertThrows(CategoryNotFoundException.class,
                () -> productService.saveProduct(request),
                "Category " + request.getCategoryName() + " not found.");
    }

    @Test
    void saveProduct_ProductObjectAlreadyExistsException() {
        //arrange setUp

        when(categoryRepository.findCategoryByCategoryName(
                request.getCategoryName()
        )).thenReturn(Optional.of(new Category()));

        when(productRepository.findProductByProductName(
                request.getProductName()
        )).thenReturn(Optional.of(new Product()));

        //act and assert
        assertThrows(ProductObjectAlreadyExistsException.class,
                () -> productService.saveProduct(request),
                "Product with name "
                        + request.getProductName() + " already exists.");
    }

}
