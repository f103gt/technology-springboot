package com.technology.category.services;

import com.technology.category.dto.CategoryDto;
import com.technology.category.dto.JsonCategoryDto;
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

import java.util.*;
import java.util.stream.Collectors;

import static com.technology.category.helper.CategoryServiceHelper.*;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(
            CategoryRepository categoryRepository) {
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
                .map(CategoryServiceHelper::createCategoryDto)
                .sorted(CategoryServiceHelper::compareCategoryDtos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<JsonCategoryDto> getAllParentCategoriesWithChildren() {
        List<Category> categories = categoryRepository.findAll();
        List<JsonCategoryDto> finalList = new ArrayList<>();
        Set<Category> isPresent = new HashSet<>();
        for (Category category : categories) {
            if (!isPresent.contains(category)) {
                finalList.add(JsonCategoryDto.builder()
                        .categoryName(category.getCategoryName())
                        .childCategories(
                                category.getChildCategories().stream()
                                        .map(childCategory -> JsonCategoryDto.builder()
                                                .categoryName(childCategory.getCategoryName())
                                                .build()).collect(Collectors.toList()))
                        .build());
                isPresent.add(category);
                if (!category.getChildCategories().isEmpty()) {
                    isPresent.addAll(category.getChildCategories());
                }
            }
        }
        return finalList;
    }

   /* private void categoriesArePresent(Map<Category, Boolean> isPresent, List<Category> categories,) {

    }*/

    private void createParentOrChildCategory(CategoryRegistrationRequest request) {
        String parentCategoryName = request.getParentCategoryName();
        if (parentCategoryName == null || parentCategoryName.trim().isEmpty()) {
            createParentCategory(request);
        } else {
            createChildCategory(request);
        }
    }

    private void createParentCategory(CategoryRegistrationRequest request) {
        String categoryName = getCategoryName(request);
        categoryRepository.save(
                Category.builder()
                        .categoryName(categoryName)
                        .build());
    }

    private Category findParentCategoryIfPresent(String parentCategoryName) {
        return categoryRepository
                .findCategoryByCategoryName(parentCategoryName)
                .orElseThrow(() -> new ParentCategoryNotFoundException(
                        "Parent category " + parentCategoryName + " not found. " +
                                "Check it for misspelling or try creating the mentioned parent category " +
                                "before adding a child one"));
    }

    private void createChildCategory(CategoryRegistrationRequest request) {
        String parentCategoryName = getParentCategoryName(request);
        String categoryName = getCategoryName(request);
        Category parentCategory = findParentCategoryIfPresent(parentCategoryName);
        categoryRepository.save(
                Category.builder()
                        .parentCategory(parentCategory)
                        .categoryName(categoryName)
                        .build());

    }
}
