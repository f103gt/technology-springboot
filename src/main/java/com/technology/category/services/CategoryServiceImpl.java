package com.technology.category.services;

import com.technology.category.dto.CategoryDto;
import com.technology.category.exceptions.CategoryAlreadyExistsException;
import com.technology.category.exceptions.CategoryNotFoundException;
import com.technology.category.exceptions.ParentCategoryNotFoundException;
import com.technology.category.helper.CategoryServiceHelper;
import com.technology.category.models.Category;
import com.technology.category.registration.request.CategoryRegistrationRequest;
import com.technology.category.repositories.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public void saveCategory(CategoryRegistrationRequest request) {
        String categoryName = getCategoryName(request);
        if (categoryRepository.findCategoryByCategoryName(categoryName).isPresent()) {
            throw new CategoryAlreadyExistsException(
                    "Category " + categoryName + " already exists");
        }
        createParentOrChildCategory(request);
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
                        "Category " + finalCategoryName + " not found."));
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::createCategoryDto)
                .sorted(CategoryServiceHelper::compareCategoryDtos)
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
            CategoryRegistrationRequest request) {
        String categoryName = getCategoryName(request);
        categoryRepository.save(
                Category.builder()
                        .categoryName(categoryName)
                        .build());
    }

    private String getCategoryName(CategoryRegistrationRequest request){
        return request.getCategoryName().trim();
    }
    private String getParentCategoryName(CategoryRegistrationRequest request){
        return request.getParentCategoryName().trim();
    }

    private void createChildCategory(CategoryRegistrationRequest request) {

        String parentCategoryName = getParentCategoryName(request);
        String categoryName = getCategoryName(request);
        Category category = categoryRepository
                .findCategoryByCategoryName(parentCategoryName)
                .orElseThrow(() -> new ParentCategoryNotFoundException(
                        "Parent category " + parentCategoryName + " not found. " +
                                "Check it for misspelling or try creating the mentioned parent category " +
                                "before adding a child one"));
        categoryRepository.save(
                Category.builder()
                        .parentCategory(category)
                        .categoryName(categoryName)
                        .build());

    }
}
