package com.technology.category.services;

import com.technology.category.registration.request.CategoryRegistrationRequest;

public interface CategoryService {
    void saveCategory(CategoryRegistrationRequest categoryRegistrationRequest);
}
