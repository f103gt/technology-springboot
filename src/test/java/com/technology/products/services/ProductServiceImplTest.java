package com.technology.products.services;

import com.technology.category.models.Category;
import com.technology.category.repositories.CategoryRepository;
import com.technology.products.models.Product;
import com.technology.products.registration.request.ProductRegistrationRequest;
import com.technology.products.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    protected ProductRepository productRepository;

    @Mock
    protected CategoryRepository categoryRepository;

    @InjectMocks
    protected ProductServiceImpl productService;

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
        verify(productRepository,times(1))
                .save(any(Product.class));
    }

    @Test
    void saveProduct_Throws_CategoryNotFoundException() {
    }

    @Test
    void deleteProduct() {
    }

    @Test
    void getAllProducts() {
    }
}