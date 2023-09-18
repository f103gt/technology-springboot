package com.technology.products.services;

import com.technology.category.exceptions.CategoryNotFoundException;
import com.technology.category.models.Category;
import com.technology.category.repositories.CategoryRepository;
import com.technology.products.exceptions.ProductObjectAlreadyExistsException;
import com.technology.products.models.Product;
import com.technology.products.registration.request.ProductRegistrationRequest;
import com.technology.products.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    protected ProductRepository productRepository;

    @Mock
    protected CategoryRepository categoryRepository;

    @InjectMocks
    protected ProductServiceImpl productService;
}