package com.technology.category.services;

import com.technology.category.dto.CategoryDto;
import com.technology.category.registration.request.CategoryRegistrationRequest;

import java.util.List;

public interface CategoryService {
    void saveCategory(CategoryRegistrationRequest categoryRegistrationRequest);
    void deleteCategory(String categoryName);

    List<CategoryDto> getAllCategories();
}
