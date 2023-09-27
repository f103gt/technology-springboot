package com.technology.category.services;

import com.technology.category.dto.CategoryDto;
import com.technology.category.exceptions.CategoryAlreadyExistsException;
import com.technology.category.exceptions.CategoryNotFoundException;
import com.technology.category.exceptions.ParentCategoryNotFoundException;
import com.technology.category.models.Category;
import com.technology.category.registration.request.CategoryRegistrationRequest;
import com.technology.category.repositories.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    //TODO refactor the method to adhere to SOLID principles
    //TODO check if the category to inserted does not exist already
    @Override
    @Transactional
    public void saveCategory(CategoryRegistrationRequest categoryRegistrationRequest) {
        String categoryName = categoryRegistrationRequest.getCategoryName().trim();
        if (categoryRepository.findCategoryByCategoryName(categoryName).isPresent()) {
            throw new CategoryAlreadyExistsException(
                    "Category " + categoryName + " already exists");
        }
        createParentOrChildCategory(categoryRegistrationRequest);
    }

    private void createParentOrChildCategory(CategoryRegistrationRequest request) {
        String parentCategoryName = request.getParentCategoryName();
        if (parentCategoryName == null || parentCategoryName.trim().isEmpty()) {
            createParentCategory(request);
        } else {
            createChildCategory(request);
        }
    }


    @Override
    @Transactional
    public void deleteCategory(String categoryName) {
        String finalCategoryName = categoryName.trim();
        Category category = categoryRepository
                .findCategoryByCategoryName(finalCategoryName)
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category " + finalCategoryName + " not found.")));
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::createCategoryDto)
                .sorted(this::compareCategoryDtos)
                .collect(Collectors.toList());
    }

    private CategoryDto createCategoryDto(Category category) {
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

    private int compareCategoryDtos(CategoryDto categoryDto1, CategoryDto categoryDto2) {
        int parentCategoriesComparison = categoryDto1.getParentCategoryName()
                .compareToIgnoreCase(categoryDto2.getParentCategoryName());
        if (parentCategoriesComparison == 0) {
            return categoryDto1.getCategoryName()
                    .compareToIgnoreCase(categoryDto2.getCategoryName());
        }
        return parentCategoriesComparison;
    }


    private void createParentCategory(
            CategoryRegistrationRequest categoryRegistrationRequest) {
        categoryRepository.save(
                Category.builder()
                        .categoryName(categoryRegistrationRequest.getCategoryName().trim())
                        .build());
    }

    private void createChildCategory(
            CategoryRegistrationRequest request) {
        String parentCategoryName = request.getParentCategoryName().trim();
        String categoryName = request.getCategoryName().trim();
        Category category = categoryRepository.findCategoryByCategoryName(parentCategoryName)
                .orElseThrow(() -> new ParentCategoryNotFoundException(
                        "Parent category " + request.getParentCategoryName().trim() +
                                " not found. Check it for misspelling or try creating the mentioned parent category " +
                                "before adding a child one"));
        categoryRepository.save(
                Category.builder()
                        .parentCategory(category)
                        .categoryName(categoryName)
                        .build());

    }
}
