package com.technology.category.services;

import com.technology.category.exceptions.CategoryNotFoundException;
import com.technology.category.exceptions.ParentCategoryNotFoundException;
import com.technology.category.models.Category;
import com.technology.category.registration.request.CategoryRegistrationRequest;
import com.technology.category.repositories.CategoryRepository;
import com.technology.products.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    //TODO refactor the method to adhere to SOLID principles
    @Override
    @Transactional
    public void saveCategory(CategoryRegistrationRequest categoryRegistrationRequest) {
        String parentCategoryName = categoryRegistrationRequest.getCategoryName();
        if (parentCategoryName == null) {
            createParentCategory(categoryRegistrationRequest);
        } else {
            createChildCategory(categoryRegistrationRequest);
        }
    }

    @Override
    @Transactional
    public void deleteCategory(String categoryName) {
        Optional<Category> categoryOptional = categoryRepository.findCategoryByCategoryName(categoryName);
        if (categoryOptional.isEmpty()) {
            throw new CategoryNotFoundException(
                    "Category " + categoryName + " not found.");
        }
        Category category = categoryOptional.get();

        //TODO add queries to repositories for the following methods
        productRepository.deleteAllByCategoryId(category.getId());
        categoryRepository.deleteCategoryByCategoryName(categoryName);
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
                categoryRepository.findCategoryByCategoryName(
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
