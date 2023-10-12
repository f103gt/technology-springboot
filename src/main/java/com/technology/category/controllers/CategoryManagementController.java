package com.technology.category.controllers;

import com.technology.category.dto.CategoryDto;
import com.technology.category.dto.CategoryToDeleteDto;
import com.technology.category.dto.JsonCategoryDto;
import com.technology.category.models.Category;
import com.technology.category.registration.request.CategoryRegistrationRequest;
import com.technology.category.services.CategoryService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryManagementController {
    private final CategoryService categoryService;

    public CategoryManagementController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping ("/api/v1/all-categories")
    public ResponseEntity<List<JsonCategoryDto>> allCategories() {

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(categoryService.getAllParentCategoriesWithChildren());
    }

    @PostMapping ("/manager/add-category")
    public ResponseEntity<String> addCategory(@RequestBody CategoryRegistrationRequest categoryRegistrationRequest) {
        categoryService.saveCategory(categoryRegistrationRequest);
        return ResponseEntity.ok("A new category was successfully added");
    }

    @DeleteMapping ("/manager/delete-category")
    public ResponseEntity<String> deleteCategory(@RequestBody CategoryToDeleteDto categoryName) {
        categoryService.deleteCategory(categoryName.getCategoryName());
        return ResponseEntity.ok("The category was successfully deleted");
    }
}
