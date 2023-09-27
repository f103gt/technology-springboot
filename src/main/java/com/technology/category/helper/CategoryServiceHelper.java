package com.technology.category.helper;

import com.technology.category.dto.CategoryDto;
import com.technology.category.models.Category;
import com.technology.category.registration.request.CategoryRegistrationRequest;

public class CategoryServiceHelper {
    public static int compareCategoryDtos(CategoryDto categoryDto1, CategoryDto categoryDto2) {
        int parentCategoriesComparison = categoryDto1.getParentCategoryName()
                .compareToIgnoreCase(categoryDto2.getParentCategoryName());
        if (parentCategoriesComparison == 0) {
            return categoryDto1.getCategoryName()
                    .compareToIgnoreCase(categoryDto2.getCategoryName());
        }
        return parentCategoriesComparison;
    }

    public static CategoryDto createCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        if (category.getParentCategory() == null) {
            categoryDto.setParentCategoryName("");
        } else {
            categoryDto.setParentCategoryName(
                    category.getParentCategory().getCategoryName());
        }
        categoryDto.setCategoryName(category.getCategoryName());
        return categoryDto;
    }

    public static String getCategoryName(CategoryRegistrationRequest request) {
        return request.getCategoryName().trim();
    }

    public static String getParentCategoryName(CategoryRegistrationRequest request) {
        return request.getParentCategoryName().trim();
    }

}
