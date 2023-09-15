package com.technology.category.services;

import com.technology.category.repositories.CategoryRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    protected CategoryRepository categoryRepository;

    @InjectMocks
    protected CategoryServiceImpl categoryService;

}