package com.technology.management;

import com.technology.category.dto.CategoryDto;
import com.technology.category.models.Category;
import com.technology.category.registration.request.CategoryRegistrationRequest;
import com.technology.category.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class CategoryManagementController {
    private final CategoryService categoryService;

    public CategoryManagementController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/manager/all-categories")
    public List<CategoryDto> allCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/manager/add-category")
    public ResponseEntity<String> addCategory(@RequestBody CategoryRegistrationRequest categoryRegistrationRequest) {
        categoryService.saveCategory(categoryRegistrationRequest);
        return ResponseEntity.ok("A new category was successfully added");
    }

    @PostMapping("/manager/delete-category")
    public ResponseEntity<String> deleteCategory(@RequestBody String categoryName) {
        categoryService.deleteCategory(categoryName);
        return ResponseEntity.ok("The category was successfully deleted");
    }
}
