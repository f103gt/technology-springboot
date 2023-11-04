/*
package com.technology.category.services;

import com.technology.category.exceptions.CategoryNotFoundException;
import com.technology.category.models.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class CategoryServiceImplCategoryDeleteTest extends CategoryServiceImplTest{
    private Category category;
    private String categoryName;
    @BeforeEach
    void setUp(){
        categoryName = "TestCategory";
        category = new Category();
        category.setCategoryName(categoryName);
    }
    @Test
    void deleteCategory_DeletesCategory_IfCategoryPresent() {
        //arrange(setUp)
        //act
        when(categoryRepository
                .findCategoryByCategoryName(categoryName))
                .thenReturn(Optional.of(category));
        categoryService.deleteCategory(categoryName);

        //assert
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategory_Throws_CategoryNotFoundException() {
        //arrange(setUp)
        //act
        when(categoryRepository
                .findCategoryByCategoryName(categoryName))
                .thenReturn(Optional.empty());
        //assert
        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(categoryName),
                "Category " + categoryName + " not found.");
    }
}
*/
