package com.technology.category.services;

import com.technology.category.exceptions.CategoryAlreadyExistsException;
import com.technology.category.exceptions.ParentCategoryNotFoundException;
import com.technology.category.models.Category;
import com.technology.category.registration.request.CategoryRegistrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CategoryServiceImplCategoryCreationTest extends CategoryServiceImplTest {
    private CategoryRegistrationRequest request;

    @BeforeEach
    void setUp() {
        request = new CategoryRegistrationRequest();
        request.setCategoryName("TestCategory");
    }

    @Test
    void saveCategory_SavesCategory_WhenCategoryIsValidAndHasNoParentCategory() {
        //arrange
        request.setParentCategoryName(null);
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
        request.setParentCategoryName("ParentTestCategory");
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
        //act
        when(categoryRepository
                .findCategoryByCategoryName(request.getCategoryName()))
                .thenReturn((Optional.of(new Category())));
        //assert
        assertThrows(CategoryAlreadyExistsException.class,
                () -> categoryService.saveCategory(request),
                "Category " + request.getCategoryName() + " already exists");
    }

    @Test
    void saveCategory_Throws_ParentCategoryNotFoundException() {
        //arrange
        request.setParentCategoryName("NotFoundParentCategory");

        when(categoryRepository
                .findCategoryByCategoryName(request.getCategoryName()))
                .thenReturn((Optional.empty()));
        when(categoryRepository
                .findCategoryByCategoryName(request.getParentCategoryName()))
                .thenReturn(Optional.empty());
        //act and assert
        assertThrows(ParentCategoryNotFoundException.class,
                () -> categoryService.saveCategory(request),
                "Parent category " + request.getParentCategoryName() +
                        " not found. Check it for misspelling or try creating the mentioned parent category " +
                        "before adding a child one");
    }

}
