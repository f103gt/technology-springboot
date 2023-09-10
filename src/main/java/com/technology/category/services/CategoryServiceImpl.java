package com.technology.category.services;

import com.technology.category.exceptions.ParentCategoryNotFoundException;
import com.technology.category.models.Category;
import com.technology.category.registration.request.CategoryRegistrationRequest;
import com.technology.category.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    //TODO refactor the method to adhere to SOLID principles
    @Override
    public void saveCategory(CategoryRegistrationRequest categoryRegistrationRequest) {
        String parentCategoryName = categoryRegistrationRequest.getCategoryName();
        if (parentCategoryName == null) {
            createParentCategory(categoryRegistrationRequest);
        } else {
            createChildCategory(categoryRegistrationRequest);
        }
    }

    private void createParentCategory(
            CategoryRegistrationRequest categoryRegistrationRequest) {
        categoryRepository.save(
                Category.builder()
                        .categoryName(categoryRegistrationRequest.getCategoryName())
                        .build());
    }

    private void createChildCategory(
            CategoryRegistrationRequest categoryRegistrationRequest) {
        Optional<Category> parentCategoryOptional =
                categoryRepository.findCategoriesByCategoryName(
                        categoryRegistrationRequest.getParentCategoryName()
                );
        parentCategoryOptional
                .ifPresentOrElse(category -> {
                            categoryRepository.save(
                                    Category.builder()
                                            .parentCategory(category)
                                            .categoryName(categoryRegistrationRequest.getCategoryName())
                                            .build());
                        },
                        () -> {
                            throw new ParentCategoryNotFoundException(
                                    "Parent category " + categoryRegistrationRequest.getParentCategoryName() +
                                            " not found. Check it for misspelling or try creating the mentioned parent category " +
                                            "before adding a child one");
                        });
    }
}
