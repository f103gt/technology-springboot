package com.technology.category.services;

import com.technology.category.exceptions.CategoryAlreadyExistsException;
import com.technology.category.exceptions.ParentCategoryNotFoundException;
import com.technology.category.models.Category;
import com.technology.category.registration.request.CategoryRegistrationRequest;
import com.technology.category.repositories.CategoryRepository;
import com.technology.products.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void saveCategory_SavesCategory_WhenCategoryIsValidAndHasNoParentCategory() {
        //arrange
        CategoryRegistrationRequest request = new CategoryRegistrationRequest();
        request.setParentCategoryName(null);
        request.setCategoryName("TestCategory");
        //act
        when(categoryRepository
                .findCategoryByCategoryName(request.getCategoryName()))
                .thenReturn(Optional.empty());
        categoryService.saveCategory(request);

        //assert
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void saveCategory_SavesCategory_WhenCategoryIsValidAndHasParentCategory() {
        //arrange
        CategoryRegistrationRequest request = new CategoryRegistrationRequest();
        request.setParentCategoryName("ParentTestCategory");
        request.setCategoryName("TestCategory");
        //act
        when(categoryRepository
                .findCategoryByCategoryName(request.getCategoryName()))
                .thenReturn(Optional.empty());
        when(categoryRepository
                .findCategoryByCategoryName(request.getParentCategoryName()))
                .thenReturn(Optional.of(new Category()));

        categoryService.saveCategory(request);

        //assert
        verify(categoryRepository, times(1)).save(any(Category.class));
    }


    @Test
    void saveCategory_Throws_CategoryAlreadyExistsException() {
        //arrange
        CategoryRegistrationRequest request = new CategoryRegistrationRequest();
        request.setCategoryName("TestCategory");

        when(categoryRepository
                .findCategoryByCategoryName(request.getCategoryName()))
                .thenReturn((Optional.of(new Category())));
        //act and assert
        assertThrows(CategoryAlreadyExistsException.class,
                () -> categoryService.saveCategory(request),
                "Category " + request.getCategoryName() + " already exists");
    }


    @Test
    void deleteCategory() {
    }

    @Test
    void getAllCategories() {
    }
}