package com.technology.category.services;

import com.technology.category.dto.CategoryDto;
import com.technology.category.models.Category;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/*
public class CategoryServiceImplGetAllCategoriesTest extends CategoryServiceImplTest{
    @Test
    void getAllCategories_Gets_AllCategoriesSorted() {
        //arrange
        Category parentCategory = new Category();
        parentCategory.setCategoryName("Parent Category");

        Category childCategoryFirst = new Category();
        childCategoryFirst.setCategoryName("Child Category 1");
        childCategoryFirst.setParentCategory(parentCategory);

        Category childCategorySecond = new Category();
        childCategorySecond.setCategoryName("Child Category 2");
        childCategorySecond.setParentCategory(parentCategory);

        //act
        when(categoryRepository.findAll())
                .thenReturn(
                        Arrays.asList(parentCategory,
                                childCategoryFirst,
                                childCategorySecond));

        List<CategoryDto> categories = categoryService.getAllCategories();
        //assert
        assertThat(categories).hasSize(3);
        assertThat(categories.get(0).getParentCategoryName()).isEqualTo("");
        CategoryDto categoryDtoFirst = categories.get(1);
        assertThat(categoryDtoFirst)
                .extracting(CategoryDto::getParentCategoryName,
                        CategoryDto::getCategoryName)
                .containsExactly("Parent Category","Child Category 1");
        CategoryDto categoryDtoSecond = categories.get(2);
        assertThat(categoryDtoSecond)
                .extracting(CategoryDto::getParentCategoryName,
                        CategoryDto::getCategoryName)
                .containsExactly("Parent Category","Child Category 2");
    }
}
*/
