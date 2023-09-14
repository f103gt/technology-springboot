package com.technology.category.services;

import com.technology.category.models.Category;
import com.technology.category.registration.request.CategoryRegistrationRequest;
import com.technology.category.repositories.CategoryRepository;
import com.technology.products.repositories.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;

    private CategoryServiceImpl categoryService;


    @BeforeEach
    void init() throws Exception {
        categoryRepository = mock(CategoryRepository.class);
        productRepository = mock(ProductRepository.class);
        categoryService = new CategoryServiceImpl(
                categoryRepository,
                productRepository
        );
    }

    @Test
    void saveValidCategoryInputWithNoParentCategory() {
        //Arrange
        CategoryRegistrationRequest request = new CategoryRegistrationRequest();
        request.setParentCategoryName(null);
        request.setCategoryName("TestCategory");
        //act
        when(categoryRepository
                .findCategoryByCategoryName(anyString()))
                .thenReturn(Optional.empty());
        categoryService.saveCategory(request);

        //assert
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
    

    @Test
    void deleteCategory() {
    }

    @Test
    void getAllCategories() {
    }
}