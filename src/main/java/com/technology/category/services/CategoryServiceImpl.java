package com.technology.category.services;

import com.technology.category.dto.CategoryDto;
import com.technology.category.exceptions.CategoryAlreadyExistsException;
import com.technology.category.exceptions.CategoryNotFoundException;
import com.technology.category.exceptions.ParentCategoryNotFoundException;
import com.technology.category.models.Category;
import com.technology.category.registration.request.CategoryRegistrationRequest;
import com.technology.category.repositories.CategoryRepository;
import com.technology.products.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    //TODO check if the category to inserted does not exist already
    @Override
    @Transactional
    public void saveCategory(CategoryRegistrationRequest categoryRegistrationRequest) {
        if (categoryRepository.findCategoryByCategoryName(
                categoryRegistrationRequest.getCategoryName()).isEmpty()) {
            String parentCategoryName = categoryRegistrationRequest.getParentCategoryName();
            if (parentCategoryName == null) {
                createParentCategory(categoryRegistrationRequest);
            } else {
                createChildCategory(categoryRegistrationRequest);
            }
        } else {
            throw new CategoryAlreadyExistsException("Category " + categoryRegistrationRequest.getCategoryName()
                    + " already exists");
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

    @Override
    @Transactional
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> {
                    CategoryDto categoryDto = new CategoryDto();
                    if (category.getParentCategory() == null) {
                        categoryDto.setParentCategoryName("");
                    } else {
                        categoryDto.setParentCategoryName(category.getParentCategory().getCategoryName());
                    }
                    categoryDto.setCategoryName(category.getCategoryName());
                    return categoryDto;
                })
                .sorted((categoryDto1, categoryDto2) -> {
                    int parentCategoriesComparison = categoryDto1.getParentCategoryName()
                            .compareToIgnoreCase(categoryDto2.getParentCategoryName());
                    if (parentCategoriesComparison == 0) {
                        return categoryDto1.getCategoryName()
                                .compareToIgnoreCase(categoryDto2.getCategoryName());
                    }
                    return parentCategoriesComparison;
                })
                .collect(Collectors.toList());
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
