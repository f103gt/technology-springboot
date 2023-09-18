package com.technology.products.services;

import com.technology.category.repositories.CategoryRepository;
import com.technology.products.repositories.ProductRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    protected ProductRepository productRepository;

    @Mock
    protected CategoryRepository categoryRepository;

    @InjectMocks
    protected ProductServiceImpl productService;
}