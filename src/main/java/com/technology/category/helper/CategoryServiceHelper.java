package com.technology.category.helper;

import com.technology.category.dto.CategoryDto;

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

}
